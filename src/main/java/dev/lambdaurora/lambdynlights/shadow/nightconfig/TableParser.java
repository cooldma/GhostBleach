package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TableParser extends NightConfig {


    public TableParser() {
        super("AimAssist", KEY_UNBOUND, NightConfigCategory.COMBAT, "Aims at players for you.",
            new DynamicLightsInitializerSlider("Range", 2, 9, 4, 0).withDesc("How far away to start aiming."),
            new DynamicLightsInitializerSlider("Speed", 1, 500, 1, 0).withDesc("How fast to aim."),
            new DynamicLightsInitializerMode("Mode", "Head", "Center", "Feet").withDesc("Where to aim."),
            new DynamicLightsInitializerToggle("Players only", true).withDesc("If it attacks players only or other entities (such as animals, mobs, etc)."),
            new DynamicLightsInitializerSlider("Rand", -1,100,5,0).withDesc("Randomization amount."),
            new DynamicLightsInitializerToggle("Instant Aim", false).withDesc("Instantly aims at the nearest entity within a certain range.")
            );
    }
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    @Subscribe
    public void onTick(EventTick event) {

        if (mc.currentScreen instanceof HandledScreen) {
            return;
        }

        aimAtNearestEntity(mc.player, getSetting(1).asSlider().getValueInt());
    }

    public void aimAtNearestEntity(PlayerEntity player, float speed) {
        double reachDistance = getSetting(0).asSlider().getValueInt();
        Vec3d playerPos = player.getPos();
        Box playerBox = player.getBoundingBox().expand(reachDistance, reachDistance, reachDistance);

        List<Entity> nearbyEntities;
        if (getSetting(3).asToggle().getState()) {
            nearbyEntities = player.world.getEntitiesByClass(Entity.class, playerBox, entity ->
                    entity instanceof PlayerEntity && (entity != mc.player) && entity.isAttackable() && entity.isAlive());
        } else {
            nearbyEntities = player.world.getEntitiesByClass(Entity.class, playerBox, entity ->
                    entity instanceof Entity && (entity != mc.player) && entity.isAttackable() && entity.isAlive());
        }



        Entity nearestEntity = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            double distance = entity.getPos().squaredDistanceTo(playerPos);
            if (distance < nearestDistance) {
                nearestEntity = entity;
                nearestDistance = distance;
            }
        }

        if (nearestEntity != null && !isCrosshairOverEntity()) {

            if (getSetting(5).asToggle().getState()) {
                speed = 1000000;
            }

            double deltaX = nearestEntity.getX() - player.getX();
            double deltaY = 0;

            if (getSetting(2).asMode().getMode() == 0) {
                if (nearestEntity instanceof PlayerEntity) {
                    deltaY = nearestEntity.getEyeY() - player.getEyeY();
                } else {
                    deltaY = (nearestEntity.getY() + nearestEntity.getHeight() - player.getEyeY());
                }
            }

            if (getSetting(2).asMode().getMode() == 1) {
                deltaY = nearestEntity.getY() + nearestEntity.getHeight() / 2 - player.getEyeY();
            }

            if (getSetting(2).asMode().getMode() == 2) {
                deltaY = nearestEntity.getY() - player.getY() - player.getEyeHeight(player.getPose());
            }

            double deltaZ = nearestEntity.getZ() - player.getZ();

            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            float pitch = (float) Math.toDegrees(Math.atan2(-deltaY, horizontalDistance));

            float currentYaw = player.getYaw();
            float currentPitch = player.getPitch();
            float deltaYaw = MathHelper.wrapDegrees(yaw - currentYaw);
            float deltaPitch = MathHelper.wrapDegrees(pitch - currentPitch);

            float timeInterval = 1;

            float speed2;
            float maxDeltaYaw = speed * timeInterval / 1000.0f * 20;
            float maxDeltaPitch = speed * timeInterval / 1000.0f * 10;

            if (getSetting(4).asSlider().getValue() > 0) {
                speed2 = speed + (float) (Math.random() * 2 * getSetting(4).asSlider().getValue() - getSetting(4).asSlider().getValue());
                speed2 =  (speed2 * (float)horizontalDistance * getSetting(4).asSlider().getValueFloat() / 100);
                maxDeltaYaw = speed2 * timeInterval / 1000.0f * 20;
                maxDeltaPitch = speed2 * timeInterval / 1000.0f * 10;
            }


            float clampedDeltaYaw = MathHelper.clamp(deltaYaw, -maxDeltaYaw, maxDeltaYaw);
            float clampedDeltaPitch = MathHelper.clamp(deltaPitch, -maxDeltaPitch, maxDeltaPitch);

            float newYaw = currentYaw + clampedDeltaYaw;
            float newPitch = currentPitch + clampedDeltaPitch;

            player.setYaw(newYaw);
            player.setPitch(newPitch);
        }
    }

    public static boolean isCrosshairOverEntity() {
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            return true;
        } else {
            return false;
        }
    }

}
