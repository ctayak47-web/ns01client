package absolute.modules.visuals;

import absolute.Module;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "Отключает рендер нежелательных элементов.", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        // Логика включения NoRender (например, патчинг рендер-вызовов через миксины)
    }

    @Override
    public void onDisable() {
        // Логика выключения NoRender
    }
    // NoRender обычно работает через миксины, поэтому onTick/onRender здесь не требуются
}
        