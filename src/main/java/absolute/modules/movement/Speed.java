package absolute.modules.movement;

import absolute.Module;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Ускоряет игрока.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        // Логика включения Speed
    }

    @Override
    public void onDisable() {
        // Логика выключения Speed
    }

    @Override
    public void onTick() {
        // Логика для ускорения движения
    }
}
        