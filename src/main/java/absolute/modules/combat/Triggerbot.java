package absolute.modules.combat;

import absolute.Module;

public class Triggerbot extends Module {
    public Triggerbot() {
        super("Triggerbot", "Автоматически атакует, когда цель находится под прицелом.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения Triggerbot
    }

    @Override
    public void onDisable() {
        // Логика выключения Triggerbot
    }

    @Override
    public void onTick() {
        // Логика проверки цели под прицелом и атаки
    }
}
        