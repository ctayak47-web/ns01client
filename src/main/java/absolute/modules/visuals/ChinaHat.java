package absolute.modules.visuals;

import absolute.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Модуль ChinaHat для клиента NS01.
 * Рендер конуса над головой игрока. Использует буфер матриц из 1.21.4.
 * Радиус, градиент цвета, высота. Расчет через тригонометрию, вынесенный в статический массив
 * для оптимизации под Android.
 */
public class ChinaHat extends Module {

    // Параметры для конуса
    private static final float coneRadius = 0.5F;
    private static final float coneHeight = 0.5F;
    private static final int segments = 64; // Количество сегментов для круга, чем больше, тем плавнее
    private static final float[] SIN_LOOKUP = new float[segments];
    private static final float[] COS_LOOKUP = new float[segments];

    // Цвета для градиента
    private static final int startColor = 0xFF8800FF; // Фиолетовый
    private static final int endColor = 0xFF00FFFF;   // Голубой

    static {
        // Предварительный расчет синусов и косинусов для оптимизации (Android)
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            SIN_LOOKUP[i] = (float) Math.sin(angle);
            COS_LOOKUP[i] = (float) Math.cos(angle);
        }
    }

    public ChinaHat() {
        super("ChinaHat", "Отрисовывает конус над головой игрока.", Category.VISUALS);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        if (MC.player == null || MC.isPaused()) {
            return;
        }

        PlayerEntity player = MC.player;

        // Позиция игрока для рендера
        double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());

        // Начало отрисовки
        matrixStack.push();

        // Смещение к центру игрока и наверх
        matrixStack.translate(x, y + player.getHeight() + 0.25D, z); // 0.25D - небольшое смещение над головой

        // Поворот конуса для красоты (опционально)
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(System.currentTimeMillis() / 15.0F % 360));

        // Настройки рендера
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false); // Чтобы конус был виден сквозь блоки

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Matrix4f model = matrixStack.peek().getPositionMatrix();

        // Отрисовка конуса
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        // Вершина конуса
        float apexY = coneHeight;
        bufferBuilder.vertex(model, 0.0F, apexY, 0.0F)
                .color(startColor)
                .next();

        // Основание конуса (рисуем круг из треугольников, сходящихся к вершине)
        for (int i = 0; i <= segments; i++) { // <= segments, чтобы замкнуть круг
            int currentSegment = i % segments;
            float cos = COS_LOOKUP[currentSegment];
            float sin = SIN_LOOKUP[currentSegment];

            float xPos = cos * coneRadius;
            float zPos = sin * coneRadius;

            // Цветовой градиент для основания
            int color = getColor(i, segments, startColor, endColor);

            bufferBuilder.vertex(model, xPos, 0.0F, zPos)
                    .color(color)
                    .next();
        }
        Tessellator.getInstance().draw();

        // Отрисовка нижней части конуса (круга)
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(model, 0.0F, 0.0F, 0.0F) // Центр основания
                .color(getColor(0, segments, endColor, startColor)) // Смешиваем цвета
                .next();
        for (int i = segments; i >= 0; i--) { // В обратном порядке для правильной ориентации
            int currentSegment = i % segments;
            float cos = COS_LOOKUP[currentSegment];
            float sin = SIN_LOOKUP[currentSegment];

            float xPos = cos * coneRadius;
            float zPos = sin * coneRadius;

            int color = getColor(i, segments, endColor, startColor);

            bufferBuilder.vertex(model, xPos, 0.0F, zPos)
                    .color(color)
                    .next();
        }
        Tessellator.getInstance().draw();

        // Восстановление настроек рендера
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        matrixStack.pop();
    }

    /**
     * Вычисляет цвет на основе градиента.
     * @param index Текущий индекс сегмента.
     * @param totalSegments Общее количество сегментов.
     * @param color1 Начальный цвет.
     * @param color2 Конечный цвет.
     * @return Интерполированный цвет.
     */
    private int getColor(int index, int totalSegments, int color1, int color2) {
        float ratio = (float) index / totalSegments;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        int a = (int) (a1 + (a2 - a1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
        