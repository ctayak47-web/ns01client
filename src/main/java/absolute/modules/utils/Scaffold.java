package absolute.modules.utils;

import absolute.Module;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Автоматически строит блоки под ногами.", Category.UTILS);
    }

    @Override
    public void onEnable() {
        // Логика включения Scaffold
    }

    @Override
    public void onDisable() {
        // Логика выключения Scaffold
    }

    @Override
    public void onTick() {
        // Логика для строительства блоков
    }
}
        