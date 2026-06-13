package absolute.modules.visuals;

import absolute.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class ChinaHat extends Module {

    private static final float coneRadius = 0.5F;
    private static final float coneHeight = 0.5F;
    private static final int segments = 64;
    private static final float[] SIN_LOOKUP = new float[segments];
    private static final float[] COS_LOOKUP = new float[segments];

    private static final int startColor = 0xFF8800FF;
    private static final int endColor = 0xFF00FFFF;

    static {
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            SIN_LOOKUP[i] = (float) Math.sin(angle);
            COS_LOOKUP[i] = (float) Math.cos(angle);
        }
    }

    public ChinaHat() {
        super("ChinaHat", "Отрисовывает конус над головой.", Category.VISUALS);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        if (MC.player == null || MC.isPaused()) return;

        PlayerEntity player = MC.player;
        double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());

        matrixStack.push();
        matrixStack.translate(x, y + player.getHeight() + 0.25D, z);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(System.currentTimeMillis() / 15.0F % 360));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferAllocator allocator = tessellator.getBufferAllocator();
        Matrix4f model = matrixStack.peek().getPositionMatrix();

        // Отрисовка
        BufferBuilder bufferBuilder = allocator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(model, 0.0F, coneHeight, 0.0F).color(startColor);
        for (int i = 0; i <= segments; i++) {
            int seg = i % segments;
            bufferBuilder.vertex(model, COS_LOOKUP[seg] * coneRadius, 0.0F, SIN_LOOKUP[seg] * coneRadius)
                         .color(getColor(i, segments, startColor, endColor));
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        matrixStack.pop();
    }

    private int getColor(int index, int totalSegments, int color1, int color2) {
        float ratio = (float) index / totalSegments;
        int r = (int) (((color1 >> 16) & 0xFF) + (((color2 >> 16) & 0xFF) - ((color1 >> 16) & 0xFF)) * ratio);
        int g = (int) (((color1 >> 8) & 0xFF) + (((color2 >> 8) & 0xFF) - ((color1 >> 8) & 0xFF)) * ratio);
        int b = (int) ((color1 & 0xFF) + ((color2 & 0xFF) - (color1 & 0xFF)) * ratio);
        int a = (int) (((color1 >> 24) & 0xFF) + (((color2 >> 24) & 0xFF) - ((color1 >> 24) & 0xFF)) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}