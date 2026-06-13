package absolute.modules.combat;

import absolute.Module;

public class AutoLeave extends Module {
    public AutoLeave() {
        super("AutoLeave", "Автоматически выходит с сервера при низком здоровье.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения AutoLeave
    }

    @Override
    public void onDisable() {
        // Логика выключения AutoLeave
    }

    @Override
    public void onTick() {
        // Логика проверки здоровья и выхода
    }
}
        