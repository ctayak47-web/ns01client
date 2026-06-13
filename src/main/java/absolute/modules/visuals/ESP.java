package absolute.modules.visuals;

import absolute.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ESP extends Module {
    public ESP() {
        super("ESP", "Отрисовывает рамки вокруг сущностей.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        // Логика включения ESP
    }

    @Override
    public void onDisable() {
        // Логика выключения ESP
    }

    @Override
    public void onTick() {
        // Логика для ESP (например, фильтрация сущностей)
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        // Логика отрисовки рамок в 3D мире
    }
}
        