package absolute;

import net.minecraft.client.MinecraftClient;

/**
 * Базовый абстрактный класс для всех модулей клиента NS01.
 * Содержит общие свойства и методы для управления модулями.
 * Оптимизирован для Android: содержит ссылки на MinecraftClient, но не создает объекты в рендер-тике.
 */
public abstract class Module {

    protected static final MinecraftClient MC = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    protected boolean enabled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Переключает состояние модуля (вкл/выкл).
     * Вызывает onEnable() или onDisable() соответственно.
     */
    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    /**
     * Включает модуль. Вызывается при активации.
     */
    public void enable() {
        if (!enabled) {
            enabled = true;
            onEnable();
        }
    }

    /**
     * Выключает модуль. Вызывается при деактивации.
     */
    public void disable() {
        if (enabled) {
            enabled = false;
            onDisable();
        }
    }

    /**
     * Метод, вызываемый при включении модуля.
     * Здесь должна быть логика инициализации.
     */
    public void onEnable() {
        // Переопределить в дочерних классах
    }

    /**
     * Метод, вызываемый при выключении модуля.
     * Здесь должна быть логика очистки/отключения.
     */
    public void onDisable() {
        // Переопределить в дочерних классах
    }

    /**
     * Метод, вызываемый каждый игровой тик, если модуль активен.
     * Избегать создания новых объектов здесь для оптимизации Android.
     */
    public void onTick() {
        // Переопределить в дочерних классах
    }

    /**
     * Метод, вызываемый для рендера 2D-элементов (HUD), если модуль активен.
     * Вызывается из главного рендер-цикла.
     *
     * @param context Контекст отрисовки.
     * @param tickDelta Дельта времени между тиками.
     */
    public void onRender2D(net.minecraft.client.gui.DrawContext context, float tickDelta) {
        // Переопределить в дочерних классах
    }

    /**
     * Метод, вызываемый для рендера 3D-элементов в игровом мире, если модуль активен.
     * Вызывается из GameRendererMixin.
     *
     * @param matrixStack Стек матриц для трансформаций.
     * @param tickDelta Дельта времени между тиками.
     */
    public void onRender3D(net.minecraft.client.util.math.MatrixStack matrixStack, float tickDelta) {
        // Переопределить в дочерних классах
    }

    /**
     * Категории модулей для лучшей организации.
     */
    public enum Category {
        COMBAT,
        MOVEMENT,
        VISUALS,
        UTILS,
        PLAYER,
        MISC // Добавляем MISC для других функций, если потребуется
    }
}
        