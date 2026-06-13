package absolute.modules.combat;

import absolute.Module;

public class AimBot extends Module {
    public AimBot() {
        super("AimBot", "Автоматически наводит прицел на врагов.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения AimBot
    }

    @Override
    public void onDisable() {
        // Логика выключения AimBot
    }

    @Override
    public void onTick() {
        // Логика для наведения прицела
    }
}
        