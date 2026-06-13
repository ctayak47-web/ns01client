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

public class JumpCircles extends Module {
    private static final List<JumpCircle> activeCircles = new ArrayList<>();
    private static boolean wasOnGround = false;
    private static final int segments = 32;
    private static final float[] SIN = new float[segments];
    private static final float[] COS = new float[segments];

    static {
        for (int i = 0; i < segments; i++) {
            float a = (float) (2 * Math.PI * i / segments);
            SIN[i] = (float) Math.sin(a);
            COS[i] = (float) Math.cos(a);
        }
    }

    public JumpCircles() { super("JumpCircles", "Круги прыжка.", Category.VISUALS); }

    @Override
    public void onTick() {
        if (MC.player == null) return;
        boolean isOnGround = MC.player.isOnGround();
        if (isOnGround && !wasOnGround && MC.player.jumping) {
            Vec3d p = MC.player.getPos();
            activeCircles.add(new JumpCircle(p.x, p.y, p.z, System.currentTimeMillis()));
        }
        wasOnGround = isOnGround;
        activeCircles.removeIf(c -> System.currentTimeMillis() - c.creationTime > 1000);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        if (MC.player == null || activeCircles.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator t = Tessellator.getInstance();
        Matrix4f model = matrixStack.peek().getPositionMatrix();
        double viewX = MathHelper.lerp(tickDelta, MC.player.lastRenderX, MC.player.getX());
        double viewY = MathHelper.lerp(tickDelta, MC.player.lastRenderY, MC.player.getY());
        double viewZ = MathHelper.lerp(tickDelta, MC.player.lastRenderZ, MC.player.getZ());

        for (JumpCircle c : activeCircles) {
            float progress = (float)(System.currentTimeMillis() - c.creationTime) / 1000f;
            int alpha = (int)(255 * (1 - progress));
            int color = (alpha << 24) | 0xFFFFFF;

            matrixStack.push();
            matrixStack.translate(c.x - viewX, c.y - viewY + 0.01D, c.z - viewZ);

            BufferBuilder b = t.getBufferAllocator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            b.vertex(model, 0, 0, 0).color(color);
            for (int i = 0; i <= segments; i++) {
                b.vertex(model, COS[i % segments] * 0.8f * progress, 0, SIN[i % segments] * 0.8f * progress).color(color);
            }
            BufferRenderer.drawWithGlobalProgram(b.end());
            matrixStack.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static class JumpCircle {
        public final double x, y, z;
        public final long creationTime;
        public JumpCircle(double x, double y, double z, long t) { this.x = x; this.y = y; this.z = z; this.creationTime = t; }
    }
}