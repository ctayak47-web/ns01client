package absolute.modules.visuals;

import absolute.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

/**
 * Модуль TargetHUD для клиента NS01.
 * Отрисовка красивого инфо-окна текущей цели Killaura (Имя, ХП баром, броня)
 * на экране игрока через DrawContext (маппинги 1.21.4).
 * Оптимизирован для Android: минимум объектов в рендер-тике, кэширование цели.
 */
public class TargetHUD extends Module {

    private LivingEntity currentTarget;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int PADDING = 4;
    private static final int BAR_HEIGHT = 5;

    public TargetHUD() {
        super("TargetHUD", "Отображает информацию о текущей цели.", Category.VISUALS);
    }

    /**
     * Устанавливает текущую цель, которую нужно отобразить.
     * Этот метод вызывается из Killaura.
     * @param target Целевая сущность.
     */
    public void setCurrentTarget(LivingEntity target) {
        this.currentTarget = target;
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        if (MC.player == null || currentTarget == null || currentTarget.isDead()) {
            currentTarget = null; // Сброс цели, если она мертва или недействительна
            return;
        }

        // Позиция HUD на экране (например, правый нижний угол)
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int x = screenWidth - WIDTH - 10;
        int y = screenHeight - HEIGHT - 10;

        // Фон HUD (темный полупрозрачный)
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0x80000000); // Полупрозрачный черный

        TextRenderer textRenderer = MC.textRenderer;

        // Имя цели
        Text targetName = currentTarget.getName().copy().formatted(Formatting.WHITE);
        context.drawText(textRenderer, targetName, x + PADDING, y + PADDING, 0xFFFFFFFF, true);

        // Здоровье цели
        float health = currentTarget.getHealth();
        float maxHealth = currentTarget.getMaxHealth();
        float healthPercentage = health / maxHealth;

        int hpColor = ColorHelper.Argb.getArgb(
                (int) (255 * (1 - healthPercentage)), // Red component
                (int) (255 * healthPercentage),      // Green component
                0,                                   // Blue component
                255                                  // Alpha component
        ); // Градиент от зеленого к красному

        String healthText = String.format("%.1f / %.1f HP", health, maxHealth);
        context.drawText(textRenderer, healthText, x + PADDING, y + PADDING + textRenderer.fontHeight + 2, 0xFFFFFFFF, true);

        // HP бар
        int barWidth = WIDTH - 2 * PADDING;
        context.fill(x + PADDING, y + HEIGHT - BAR_HEIGHT - PADDING, x + PADDING + barWidth, y + HEIGHT - PADDING, 0xFF404040); // Фон бара
        context.fill(x + PADDING, y + HEIGHT - BAR_HEIGHT - PADDING, x + PADDING + (int) (barWidth * healthPercentage), y + HEIGHT - PADDING, hpColor); // Заполнение бара

        // Отображение брони, если цель - игрок
        if (currentTarget instanceof PlayerEntity playerTarget) {
            int armor = playerTarget.getArmor();
            String armorText = "Armor: " + armor;
            context.drawText(textRenderer, Text.literal(armorText), x + PADDING, y + PADDING + textRenderer.fontHeight * 2 + 4, 0xFFADD8E6, true); // Светло-голубой цвет

            // Отрисовка предметов брони (опционально, требует больше логики и оптимизации)
            int itemX = x + WIDTH - 20; // Примерная позиция для слота брони
            int itemY = y + PADDING;
            for (ItemStack armorStack : playerTarget.getArmorItems()) {
                if (!armorStack.isEmpty()) {
                    context.drawItem(armorStack, itemX, itemY);
                    itemY += 16; // Смещение для следующего предмета
                }
            }
        }
    }
}
        