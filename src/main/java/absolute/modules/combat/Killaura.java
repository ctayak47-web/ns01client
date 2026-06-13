package absolute.modules.combat;

import absolute.Module;
import absolute.Client;
import absolute.modules.visuals.TargetHUD;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Модуль Killaura для клиента NS01.
 * Обход Grim Anticheat: атака через правильные пакеты вращения
 * с кастомным ротационным заглаживанием.
 * Проверка дистанции (Reach 3.1-3.8 блока), валидация целей.
 * Оптимизирован для Android: избегает лишних объектов в onTick, использует статические/кэшируемые значения.
 */
public class Killaura extends Module {

    // Параметры обхода Grim
    private static float currentYaw;
    private static float currentPitch;
    private static LivingEntity target;
    private static long lastAttackTime = 0;
    private static final long attackDelay = 450; // Задержка между атаками в мс (для 2.22 CPS)
    private static final float reachMin = 3.1F;
    private static final float reachMax = 3.8F; // Grim обычно палит более 3.8

    // Параметры сглаживания вращения
    private static final float smoothingFactor = 0.7F; // Чем выше, тем быстрее поворот (0.0 - 1.0)
    private static final float randomYawOffset = 0.05F; // Небольшая рандомизация для более естественного поворота
    private static final float randomPitchOffset = 0.03F;

    public Killaura() {
        super("Killaura", "Автоматически атакует ближайших врагов. Grim Bypass.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (MC.player != null) {
            currentYaw = MC.player.getYaw();
            currentPitch = MC.player.getPitch();
        }
        target = null;
    }

    @Override
    public void onDisable() {
        target = null;
    }

    @Override
    public void onTick() {
        if (MC.player == null || MC.world == null) {
            return;
        }

        // 1. Поиск цели
        target = findTarget();

        // Обновляем TargetHUD
        TargetHUD targetHudModule = Client.getModule(TargetHUD.class);
        if (targetHudModule != null) {
            targetHudModule.setCurrentTarget(target);
        }

        if (target == null) {
            return;
        }

        // 2. Расчет и сглаживание ротаций
        float[] rotations = getRotationsToTarget(target);
        float targetYaw = rotations[0];
        float targetPitch = rotations[1];

        // Добавляем немного рандомизации
        targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5F) * 2 * randomYawOffset;
        targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5F) * 2 * randomPitchOffset;

        // Сглаживание
        currentYaw = MathHelper.lerp(smoothingFactor, currentYaw, targetYaw);
        currentPitch = MathHelper.lerp(smoothingFactor, currentPitch, targetPitch);
        currentPitch = MathHelper.clamp(currentPitch, -90, 90); // Ограничиваем Pitch

        // 3. Отправка пакетов вращения
        MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(currentYaw, currentPitch, MC.player.isOnGround()));

        // 4. Атака
        if (canAttack(target)) {
            // Повторно отправляем для точности перед атакой. Некоторые античиты могут проверять.
            MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(currentYaw, currentPitch, MC.player.isOnGround()));
            MC.interactionManager.attackEntity(MC.player, target);
            MC.player.swingHand(MC.player.getActiveHand());
            lastAttackTime = System.currentTimeMillis();
        }
    }

    /**
     * Ищет наиболее подходящую цель.
     * Приоритет: ближайший игрок, не друг, не в "стене", не мертвый.
     * @return Целевой LivingEntity или null.
     */
    private LivingEntity findTarget() {
        LivingEntity closestTarget = null;
        double closestDistanceSq = Double.MAX_VALUE;

        // Используем Stream API для эффективной фильтрации и поиска
        List<LivingEntity> potentialTargets = MC.world.getEntities()
                .stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != MC.player) // Не атакуем себя
                .filter(entity -> !entity.isDead()) // Не атакуем мертвых
                .filter(entity -> MC.player.distanceTo(entity) <= reachMax) // В пределах максимальной дистанции
                .filter(this::isValidTarget) // Дополнительная валидация
                .collect(Collectors.toList());

        for (LivingEntity entity : potentialTargets) {
            double distSq = MC.player.squaredDistanceTo(entity);
            if (distSq < closestDistanceSq) {
                closestDistanceSq = distSq;
                closestTarget = entity;
            }
        }
        return closestTarget;
    }

    /**
     * Валидация цели:
     * - Не свои (если бы была система друзей).
     * - Не сквозь стены (опционально, так как Grim может палить).
     * - Только игроки, мобы (зависит от настроек).
     */
    private boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof PlayerEntity && entity == MC.player) return false; // Исключаем себя
        // Добавьте логику для друзей, если у вас есть FriendManager
        // if (Client.friendManager.isFriend(entity)) return false;

        // Проверка на видимость через стены
        // Для Grim Anticheat, иногда лучше не включать проверку на стены, т.к. рассинхрон может палить.
        // Но если обход достаточно уверенный, то можно.
        // Сейчас делаем более "безопасный" вариант - только если есть прямая видимость, или цель очень близко.
        if (MC.player.distanceTo(entity) > 2.5D) { // Не проверяем прямую видимость для очень близких целей
            if (!canPlayerSeeEntity(MC.player, entity)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, может ли игрок видеть сущность (базовый Raycast).
     * @param player Игрок, проверяющий видимость.
     * @param entity Сущность, которую нужно проверить на видимость.
     * @return true, если сущность видна, false в противном случае.
     */
    private boolean canPlayerSeeEntity(PlayerEntity player, Entity entity) {
        Vec3d eyePos = player.getEyePos();
        Vec3d targetPos = entity.getEyePos(); // Центр головы сущности

        // Создаем RaycastContext
        RaycastContext context = new RaycastContext(
                eyePos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        );

        // Выполняем Raycast
        HitResult hitResult = player.getWorld().raycast(context);

        // Если HitResult - это сущность, то это наша цель (если она находится в поле зрения)
        // Или если hitResult.getType() == HitResult.Type.MISS, значит ничего не заблокировало обзор
        return hitResult.getType() == HitResult.Type.MISS ||
                (hitResult instanceof EntityHitResult && ((EntityHitResult) hitResult).getEntity() == entity);
    }


    /**
     * Рассчитывает Yaw и Pitch для наведения на цель.
     * @param entity Цель.
     * @return Массив [yaw, pitch].
     */
    private float[] getRotationsToTarget(Entity entity) {
        Vec3d playerEyePos = MC.player.getEyePos();
        Vec3d targetVec = entity.getBoundingBox().getCenter(); // Наводимся на центр bounding box

        double diffX = targetVec.x - playerEyePos.x;
        double diffY = targetVec.y - playerEyePos.y;
        double diffZ = targetVec.z - playerEyePos.z;

        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(diffY, distXZ)));

        return new float[]{yaw, pitch};
    }

    /**
     * Проверяет, можно ли атаковать цель с учетом дистанции и кулдауна.
     * @param entity Цель.
     * @return true, если атака возможна.
     */
    private boolean canAttack(LivingEntity entity) {
        // Проверка дистанции
        double distance = MC.player.distanceTo(entity);
        if (distance > reachMax || distance < reachMin) {
            return false;
        }

        // Проверка кулдауна
        long timeSinceLastAttack = System.currentTimeMillis() - lastAttackTime;
        if (timeSinceLastAttack < attackDelay) {
            return false;
        }

        // Кулдаун игрока (для 1.9+ боевой системы)
        if (MC.player.getAttackCooldownProgress(0.5F) < 1.0F) { // 0.5F для предсказания следующего тика
            return false;
        }

        return true;
    }
}
        