package absolute.modules.combat;

import absolute.Module;

public class FastBow extends Module {
    public FastBow() {
        super("FastBow", "Быстрая стрельба из лука.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения FastBow
    }

    @Override
    public void onDisable() {
        // Логика выключения FastBow
    }

    @Override
    public void onTick() {
        // Логика ускорения стрельбы
    }
}
        