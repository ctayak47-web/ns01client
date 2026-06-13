package absolute.modules.visuals;

import absolute.Module;

public class FullBright extends Module {
    private float oldGamma;

    public FullBright() {
        super("FullBright", "Максимальная яркость в темноте.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        if (MC.options != null) {
            oldGamma = MC.options.getGamma().getValue().floatValue();
            MC.options.getGamma().setValue(20.0); // Установка максимальной яркости
            MC.options.write(); // Сохранение настроек
        }
    }

    @Override
    public void onDisable() {
        if (MC.options != null) {
            MC.options.getGamma().setValue((double) oldGamma); // Восстановление старой яркости
            MC.options.write(); // Сохранение настроек
        }
    }

    // FullBright не требует onTick или onRender2D/3D
}
        