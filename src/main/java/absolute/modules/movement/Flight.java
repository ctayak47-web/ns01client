package absolute.modules.movement;

import absolute.Module;

public class Flight extends Module {
    public Flight() {
        super("Flight", "Позволяет летать.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        // Логика включения Flight
    }

    @Override
    public void onDisable() {
        // Логика выключения Flight
    }

    @Override
    public void onTick() {
        // Логика для полета
    }
}
        