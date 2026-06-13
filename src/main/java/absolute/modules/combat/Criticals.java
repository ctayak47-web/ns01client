package absolute.modules.combat;

import absolute.Module;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Всегда наносит критические удары.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        // Логика включения Criticals
    }

    @Override
    public void onDisable() {
        // Логика выключения Criticals
    }

    @Override
    public void onTick() {
        // Логика для совершения критических ударов (например, изменение пакетов движения)
    }
}
        