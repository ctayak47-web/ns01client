package absolute.modules.visuals;

import absolute.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Модуль JumpCircles для клиента NS01.
 * Генерация и плавное затухание (альфа-канал) расширяющихся 3D-кругов
 * под ногами в точке прыжка на земле.
 * Оптимизирован для Android: использует статические массивы для тригонометрии,
 * эффективно управляет списком кругов, минимизирует создание объектов в рендер-тике.
 */
public class JumpCircles extends Module {

    private static final List<JumpCircle> activeCircles = new ArrayList<>();
    private static boolean wasOnGround = false;

    // Параметры для кругов
    private static final int segments = 32; // Количество сегментов для круга
    private static final float[] SIN_LOOKUP = new float[segments];
    private static final float[] COS_LOOKUP = new float[segments];

    // Параметры анимации
    private static final long FADE_DURATION = 1000; // мс
    private static final float MAX_RADIUS = 0.8F; // Максимальный радиус круга
    private static final int BASE_COLOR = 0xFFFFFFFF; // Белый, альфа будет меняться

    static {
        // Предварительный расчет синусов и косинусов для оптимизации (Android)
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            SIN_LOOKUP[i] = (float) Math.sin(angle);
            COS_LOOKUP[i] = (float) Math.cos(angle);
        }
    }

    public JumpCircles() {
        super("JumpCircles", "Отображает круги при прыжке.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        activeCircles.clear();
        wasOnGround = false;
    }

    @Override
    public void onTick() {
        if (MC.player == null) {
            return;
        }

        boolean isOnGround = MC.player.isOnGround();

        if (isOnGround && !wasOnGround && MC.player.jumping) { // Игрок только что прыгнул с земли
            Vec3d pos = MC.player.getPos();
            activeCircles.add(new JumpCircle(pos.x, pos.y, pos.z, System.currentTimeMillis()));
        }

        wasOnGround = isOnGround;

        // Удаление старых кругов
        activeCircles.removeIf(circle -> System.currentTimeMillis() - circle.creationTime > FADE_DURATION);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        if (MC.player == null || MC.isPaused() || activeCircles.isEmpty()) {
            return;
        }

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false); // Чтобы круги были видны сквозь блоки

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Matrix4f model = matrixStack.peek().getPositionMatrix();

        double viewX = MathHelper.lerp(tickDelta, MC.player.lastRenderX, MC.player.getX());
        double viewY = MathHelper.lerp(tickDelta, MC.player.lastRenderY, MC.player.getY());
        double viewZ = MathHelper.lerp(tickDelta, MC.player.lastRenderZ, MC.player.getZ());

        for (JumpCircle circle : activeCircles) {
            long elapsedTime = System.currentTimeMillis() - circle.creationTime;
            if (elapsedTime > FADE_DURATION) {
                continue; // Круг уже должен быть удален, но на всякий случай
            }

            float progress = (float) elapsedTime / FADE_DURATION;
            float currentRadius = MAX_RADIUS * progress;
            int alpha = (int) (255 * (1 - progress)); // Альфа от 255 до 0

            int color = (alpha << 24) | (BASE_COLOR & 0x00FFFFFF); // Только альфа меняется

            matrixStack.push();

            // Смещение к центру круга
            matrixStack.translate(circle.x - viewX, circle.y - viewY + 0.01D, circle.z - viewZ); // +0.01D для небольшого поднятия над землей

            // Отрисовка круга
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(model, 0.0F, 0.0F, 0.0F).color(color).next(); // Центр круга

            for (int i = 0; i <= segments; i++) {
                int currentSegment = i % segments;
                float cos = COS_LOOKUP[currentSegment];
                float sin = SIN_LOOKUP[currentSegment];

                float xPos = cos * currentRadius;
                float zPos = sin * currentRadius;

                bufferBuilder.vertex(model, xPos, 0.0F, zPos).color(color).next();
            }
            Tessellator.getInstance().draw();

            matrixStack.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    /**
     * Внутренний класс для хранения информации о каждом круге.
     */
    private static class JumpCircle {
        public final double x, y, z;
        public final long creationTime;

        public JumpCircle(double x, double y, double z, long creationTime) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.creationTime = creationTime;
        }
    }
}
        