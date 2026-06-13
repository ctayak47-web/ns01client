package absolute.modules.visuals;

import absolute.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", "Улучшенные ники игроков.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        // Логика включения NameTags
    }

    @Override
    public void onDisable() {
        // Логика выключения NameTags
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float tickDelta) {
        // Логика отрисовки улучшенных ников
    }
}
        