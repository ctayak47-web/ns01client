package absolute;

import absolute.modules.ModuleManager;
import net.fabricmc.api.ModInitializer; // Добавляем импорт для Fabric Entrypoint

/**
 * Главный класс клиента NS01.
 * Инициализирует все менеджеры и предоставляет к ним глобальный доступ.
 * Оптимизирован для Android: все менеджеры статичны и инициализируются один раз.
 * Также служит как основной ModInitializer для Fabric.
 */
public class Client implements ModInitializer { // Реализуем ModInitializer

    public static final String NAME = "NS01";
    public static final String VERSION = "1.21.4-Bypass";
    public static final String MOD_ID = "ns01"; // Идентификатор мода

    public static ModuleManager modules; // Главный менеджер модулей

    @Override
    public void onInitialize() {
        // Вызывается Fabric при загрузке мода
        init();
    }

    /**
     * Вызывается при инициализации клиента.
     */
    public static void init() {
        // ModuleManager уже содержит статическую инициализацию модулей.
        // Здесь мы просто могли бы создать экземпляры отдельных менеджеров, если бы они были.
        // Поскольку у нас один ModuleManager, он и будет центральной точкой.
        // ModuleManager сам статически инициализирует список модулей.
        // Здесь мы просто вызываем его методы, если бы требовалась динамическая инициализация
        // или настройки.
        // Для удобства, мы можем создать фиктивный экземпляр или просто ссылаться на статические методы.
        // Для данной структуры, где все статично в ModuleManager, можно обойтись и без явного присвоения,
        // но оставим для демонстрации и возможного будущего расширения.
        modules = new ModuleManager(); // Для удобства, чтобы было к чему обращаться через Client.modules

        // Здесь можно было бы зарегистрировать хуки событий для ModuleManager
        // Пример (псевдокод, зависит от вашей EventBus системы):
        // EventBus.getInstance().register(event -> ModuleManager.onClientTick());
        // EventBus.getInstance().register(event -> ModuleManager.onRender2D(event.getContext(), event.getTickDelta()));
        // EventBus.getInstance().register(event -> ModuleManager.onRender3D(event.getMatrixStack(), event.getTickDelta()));

        System.out.println("[" + NAME + "] " + VERSION + " Initialized!");
    }

    // Для удобства доступа к модулям:
    public static <T extends Module> T getModule(Class<T> clazz) {
        if (modules == null) {
            // Это может произойти, если модуль пытаются получить до полной инициализации
            // В реальном клиенте нужно убедиться, что init() вызывается до любого использования.
            init();
        }
        return ModuleManager.getModule(clazz);
    }
}
        