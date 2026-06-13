package absolute.modules.combat;

import absolute.Module;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "Исключает ботов из целей Killaura.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения AntiBot
    }

    @Override
    public void onDisable() {
        // Логика выключения AntiBot
    }

    @Override
    public void onTick() {
        // Логика обнаружения и фильтрации ботов
    }
}
        