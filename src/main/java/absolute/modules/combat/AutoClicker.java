package absolute.modules.combat;

import absolute.Module;

public class AutoClicker extends Module {
    public AutoClicker() {
        super("AutoClicker", "Автоматически кликает при удерживании кнопки атаки.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения AutoClicker
    }

    @Override
    public void onDisable() {
        // Логика выключения AutoClicker
    }

    @Override
    public void onTick() {
        // Логика для автоматических кликов
    }
}
        