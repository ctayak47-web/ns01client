package absolute.modules.visuals;

import absolute.Module;
import net.minecraft.client.util.math.MatrixStack;

public class BlockESP extends Module {
    public BlockESP() {
        super("BlockESP", "Отображает выбранные блоки.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        // Логика включения BlockESP
    }

    @Override
    public void onDisable() {
        // Логика выключения BlockESP
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        // Логика отрисовки блоков
    }
}
        