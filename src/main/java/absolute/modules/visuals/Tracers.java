package absolute.modules.visuals;

import absolute.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", "Линии до сущностей.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        // Логика включения Tracers
    }

    @Override
    public void onDisable() {
        // Логика выключения Tracers
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        // Логика отрисовки линий до сущностей
    }
}
        