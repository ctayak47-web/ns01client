package absolute.modules;

import absolute.Module;
import absolute.modules.combat.*;
import absolute.modules.movement.Flight;
import absolute.modules.movement.Speed;
import absolute.modules.utils.Freecam;
import absolute.modules.utils.Scaffold;
import absolute.modules.visuals.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер модулей клиента NS01.
 * Регистрирует все доступные модули и предоставляет функционал для их получения и управления.
 * Оптимизирован для Android: список модулей статичен, методы получения активных модулей эффективно используют Stream API.
 */
public class ModuleManager {

    private static final List<Module> modules = new ArrayList<>();

    // Статическая инициализация всех модулей
    static {
        // COMBAT
        modules.add(new Killaura());
        modules.add(new Mobaura());
        modules.add(new AntiBot());
        modules.add(new Triggerbot());
        modules.add(new AutoLeave());
        modules.add(new FastBow());
        modules.add(new Criticals());
        modules.add(new VelocityGrimBypass());
        modules.add(new AutoClicker());
        modules.add(new AimBot());

        // VISUALS
        modules.add(new ChinaHat());
        modules.add(new TargetHUD());
        modules.add(new JumpCircles());
        modules.add(new ClickGUI()); // Placeholder for the actual GUI manager
        modules.add(new ESP());
        modules.add(new Tracers());
        modules.add(new NameTags());
        modules.add(new FullBright());
        modules.add(new NoRender());
        modules.add(new BlockESP());

        // MOVEMENT (Заглушки)
        modules.add(new Speed());
        modules.add(new Flight());

        // UTILS (Заглушки)
        modules.add(new Freecam());
        modules.add(new Scaffold());
    }

    public ModuleManager() {
        // Конструктор, может быть пустым или использоваться для дополнительной инициализации
        // Если бы ModuleManager был синглтоном, здесь была бы проверка.
    }

    /**
     * Возвращает список всех зарегистрированных модулей.
     * @return Неизменяемый список всех модулей.
     */
    public static List<Module> getModules() {
        return modules; // Можно вернуть Collections.unmodifiableList(modules) если нужна полная неизменяемость.
    }

    /**
     * Возвращает модуль по его классу.
     * @param clazz Класс модуля, который нужно найти.
     * @param <T> Тип модуля.
     * @return Экземпляр модуля или null, если не найден.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        return null;
    }

    /**
     * Возвращает список всех активных (включенных) модулей.
     * Оптимизировано: использует Stream API для фильтрации.
     * @return Список активных модулей.
     */
    public static List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список активных модулей определенной категории.
     * Оптимизировано: использует Stream API для фильтрации.
     * @param category Категория модулей.
     * @return Список активных модулей заданной категории.
     */
    public static List<Module> getEnabledModulesByCategory(Module.Category category) {
        return modules.stream()
                .filter(module -> module.isEnabled() && module.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех модулей определенной категории, независимо от их состояния.
     * Оптимизировано: использует Stream API для фильтрации.
     * @param category Категория модулей.
     * @return Список модулей заданной категории.
     */
    public static List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Вызывает метод onTick() для всех активных модулей.
     * Используется в главном игровом цикле.
     */
    public static void onClientTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    /**
     * Вызывает метод onRender2D() для всех активных модулей категории VISUALS.
     * Используется для отрисовки HUD.
     *
     * @param context Контекст отрисовки.
     * @param tickDelta Дельта времени между тиками.
     */
    public static void onRender2D(net.minecraft.client.gui.DrawContext context, float tickDelta) {
        for (Module module : modules) {
            if (module.isEnabled() && module.getCategory() == Module.Category.VISUALS) {
                module.onRender2D(context, tickDelta);
            }
        }
    }

    /**
     * Вызывает метод onRender3D() для всех активных модулей категории VISUALS.
     * Используется для отрисовки 3D-элементов в мире.
     *
     * @param matrixStack Стек матриц для трансформаций.
     * @param tickDelta Дельта времени между тиками.
     */
    public static void onRender3D(net.minecraft.client.util.math.MatrixStack matrixStack, float tickDelta) {
        for (Module module : modules) {
            if (module.isEnabled() && module.getCategory() == Module.Category.VISUALS) {
                module.onRender3D(matrixStack, tickDelta);
            }
        }
    }
}
        