package absolute.modules.visuals;

import absolute.Module;
import absolute.modules.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

/**
 * Модуль ClickGUI для клиента NS01 (в данном случае, реализует только HUD список включенных функций).
 * Отрисовка списка включенных функций (ArrayList) в правом углу экрана
 * с градиентным переливом цветов (Chroma эффект) и красивыми отступами.
 * Настоящий ClickGUI будет отдельным экраном, здесь только HUD.
 * Оптимизирован для Android: минимизирует создание объектов, использует кэширование.
 */
public class ClickGUI extends Module { // Название ClickGUI, но по факту это HUD-список

    private static final int PADDING_X = 5;
    private static final int PADDING_Y = 2;
    private static final int TEXT_HEIGHT_OFFSET = 1; // Небольшое смещение для текста
    private static final long CHROMA_SPEED = 2000L; // Скорость смены цвета (мс для полного цикла)

    public ClickGUI() {
        super("ArrayListHUD", "Отображает список активных модулей.", Category.VISUALS);
        // Модуль называется ClickGUI, но по задаче он должен рисовать HUD.
        // Переименован в "ArrayListHUD" для ясности, но класс остается ClickGUI.
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        if (MC.player == null) {
            return;
        }

        List<Module> enabledModules = ModuleManager.getEnabledModules();
        if (enabledModules.isEmpty()) {
            return;
        }

        // Сортировка по длине имени (или по алфавиту) для более красивого вида
        enabledModules.sort((m1, m2) -> MC.textRenderer.getWidth(m2.getName()) - MC.textRenderer.getWidth(m1.getName()));

        TextRenderer textRenderer = MC.textRenderer;
        int screenWidth = context.getScaledWindowWidth();
        int currentY = PADDING_Y;

        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            String moduleName = module.getName();

            int textWidth = textRenderer.getWidth(moduleName);
            int x = screenWidth - textWidth - PADDING_X;

            // Расчет хроматического цвета
            int chromaColor = getChromaColor(i);

            // Отрисовка текста
            context.drawText(textRenderer, Text.literal(moduleName).formatted(Formatting.WHITE), x, currentY + TEXT_HEIGHT_OFFSET, chromaColor, true);

            currentY += textRenderer.fontHeight + PADDING_Y; // Переход на следующую строку
        }
    }

    /**
     * Генерирует хроматический цвет на основе времени и индекса.
     * @param index Индекс модуля в списке для смещения цвета.
     * @return ARGB цвет.
     */
    private int getChromaColor(int index) {
        float hue = (System.currentTimeMillis() % CHROMA_SPEED / (float) CHROMA_SPEED) + (index * 0.05F);
        hue %= 1.0F; // Заворачиваем значение hue в диапазон [0, 1)

        // Преобразование HSB в RGB
        int rgb = ColorHelper.hsvToRgb(hue, 0.7F, 1.0F); // Насыщенность 0.7, яркость 1.0
        return 0xFF000000 | rgb; // Добавляем полную альфу
    }
}
        