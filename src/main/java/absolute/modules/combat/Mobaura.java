package absolute.modules.combat;

import absolute.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
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
 * Модуль Mobaura для клиента NS01.
 * Атакует исключительно агрессивных и мирных мобов (исключая игроков)
 * с кастомным таймингом ударов.
 * Оптимизирован для Android: избегает лишних объектов в onTick, использует статические/кэшируемые значения.
 */
public class Mobaura extends Module {

    private static float currentYaw;
    private static float currentPitch;
    private static LivingEntity target;
    private static long lastAttackTime = 0;
    private static final long attackDelay = 400; // Немного быстрее, чем Killaura, для фарма
    private static final float reach = 4.0F; // Можно чуть больше для мобов

    // Параметры сглаживания вращения
    private static final float smoothingFactor = 0.8F; // Более агрессивный поворот, чем в Killaura
    private static final float randomYawOffset = 0.08F;
    private static final float randomPitchOffset = 0.05F;

    public Mobaura() {
        super("Mobaura", "Атакует мобов автоматически.", Category.COMBAT);
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

        target = findTarget();

        if (target == null) {
            return;
        }

        float[] rotations = getRotationsToTarget(target);
        float targetYaw = rotations[0];
        float targetPitch = rotations[1];

        targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5F) * 2 * randomYawOffset;
        targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5F) * 2 * randomPitchOffset;

        currentYaw = MathHelper.lerp(smoothingFactor, currentYaw, targetYaw);
        currentPitch = MathHelper.lerp(smoothingFactor, currentPitch, targetPitch);
        currentPitch = MathHelper.clamp(currentPitch, -90, 90);

        MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(currentYaw, currentPitch, MC.player.isOnGround()));

        if (canAttack(target)) {
            MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(currentYaw, currentPitch, MC.player.isOnGround()));
            MC.interactionManager.attackEntity(MC.player, target);
            MC.player.swingHand(MC.player.getActiveHand());
            lastAttackTime = System.currentTimeMillis();
        }
    }

    private LivingEntity findTarget() {
        LivingEntity closestTarget = null;
        double closestDistanceSq = Double.MAX_VALUE;

        List<LivingEntity> potentialTargets = MC.world.getEntities()
                .stream()
                .filter(entity -> entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) // Исключаем игроков
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> !entity.isDead())
                .filter(entity -> MC.player.distanceTo(entity) <= reach)
                .filter(this::isValidMobTarget) // Дополнительная валидация для мобов
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
     * Валидация моб-цели:
     * - Только агрессивные мобы (HostileEntity) или мирные животные (AnimalEntity).
     * - Исключает прирученных мобов, чтобы не бить своих питомцев.
     * - Проверка на видимость.
     */
    private boolean isValidMobTarget(LivingEntity entity) {
        if (entity instanceof HostileEntity) {
            // Атакуем всех агрессивных мобов
            if (!canPlayerSeeEntity(MC.player, entity)) {
                return false;
            }
            return true;
        } else if (entity instanceof AnimalEntity) {
            // Атакуем мирных животных, но исключаем прирученных
            if (entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()) {
                return false;
            }
            if (!canPlayerSeeEntity(MC.player, entity)) {
                return false;
            }
            return true;
        }
        return false; // Не атакуем другие типы сущностей
    }

    private boolean canPlayerSeeEntity(PlayerEntity player, Entity entity) {
        Vec3d eyePos = player.getEyePos();
        Vec3d targetPos = entity.getEyePos();

        RaycastContext context = new RaycastContext(
                eyePos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        );

        HitResult hitResult = player.getWorld().raycast(context);

        return hitResult.getType() == HitResult.Type.MISS ||
                (hitResult instanceof EntityHitResult && ((EntityHitResult) hitResult).getEntity() == entity);
    }

    private float[] getRotationsToTarget(Entity entity) {
        Vec3d playerEyePos = MC.player.getEyePos();
        Vec3d targetVec = entity.getBoundingBox().getCenter();

        double diffX = targetVec.x - playerEyePos.x;
        double diffY = targetVec.y - playerEyePos.y;
        double diffZ = targetVec.z - playerEyePos.z;

        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(diffY, distXZ)));

        return new float[]{yaw, pitch};
    }

    private boolean canAttack(LivingEntity entity) {
        double distance = MC.player.distanceTo(entity);
        if (distance > reach) {
            return false;
        }

        long timeSinceLastAttack = System.currentTimeMillis() - lastAttackTime;
        if (timeSinceLastAttack < attackDelay) {
            return false;
        }

        if (MC.player.getAttackCooldownProgress(0.5F) < 1.0F) {
            return false;
        }

        return true;
    }
}
        