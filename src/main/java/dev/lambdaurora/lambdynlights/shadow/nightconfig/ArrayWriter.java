package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.misc.Rot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ArrayWriter extends NightConfig {
//    public NumberSetting distance = new NumberSetting("Distance", 3, 10, 6, 0.1);
//    public NumberSetting strength = new NumberSetting("Strength", 0, 1, 0, 0.1);
//    public NumberSetting distStop = new NumberSetting("DistStop", 0, 10, 2, 0.1);
//    public BooleanSetting seeOnly = new BooleanSetting("SeeOnly", true);
//
//    public BooleanSetting yawAssist = new BooleanSetting("Yaw", true);
//    public BooleanSetting pitchAssist = new BooleanSetting("Pitch", true);

//    public AimAssist() {
//        super("AimAssist", "swag", Category.COMBAT);
//        addSettings(distance, strength, distStop, seeOnly, yawAssist, pitchAssist);
//    }

    public ArrayWriter() {
        super("AimAssist2", KEY_UNBOUND, NightConfigCategory.COMBAT, "Aims at players for you.",
                new DynamicLightsInitializerSlider("Distance", 3, 10, 6, 1).withDesc("How far away to start aiming."),
                new DynamicLightsInitializerSlider("Strength", 0, 10, 0, 1),
                new DynamicLightsInitializerSlider("DistStop", 0, 10, 2, 1),
                new DynamicLightsInitializerToggle("seeOnly", true),
                new DynamicLightsInitializerToggle("yawAssist", true),
                new DynamicLightsInitializerToggle("pitchAssist", true)
        );
    }
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isOverEntity() {
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            return true;
        } else {
            return false;
        }
    }

    @Subscribe
    public void onTick(EventTick event) {
        if(isOverEntity()) return;
        if (mc.currentScreen != null) return;


        double reachDistance = getSetting(0).asSlider().getValueInt();
        Vec3d playerPos = mc.player.getPos();
        Box playerBox = mc.player.getBoundingBox().expand(reachDistance, reachDistance, reachDistance);

        List<Entity> nearbyEntities;
        if (getSetting(3).asToggle().getState()) {
            nearbyEntities = mc.player.world.getEntitiesByClass(Entity.class, playerBox, entity ->
                    entity instanceof PlayerEntity && (entity != mc.player) && entity.isAttackable() && entity.isAlive());
        } else {
            nearbyEntities = mc.player.world.getEntitiesByClass(Entity.class, playerBox, entity ->
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

        Entity targetPlayer = nearestEntity;

        if (targetPlayer == null || (getSetting(3).asToggle().getState() && !mc.player.canSee(targetPlayer))) {
            return;
        }

        Rot targetRot = Rot.getDir(mc.player, targetPlayer.getPos());

        float yawDist = MathHelper.subtractAngles((float) targetRot.yaw(), mc.player.getYaw());
        float pitchDist = MathHelper.subtractAngles((float) targetRot.pitch(), mc.player.getPitch());

        if (Math.abs(yawDist) < getSetting(2).asSlider().getValueFloat() || Math.abs(pitchDist) < getSetting(2).asSlider().getValueFloat()) {
            return;
        }

        float yaw;
        float pitch;

        float stren = getSetting(1).asSlider().getValueFloat() / 10;

        yaw = mc.player.getYaw();
        if (Math.abs(yawDist) > stren) {
            yaw = mc.player.getYaw();
            if (yawDist < 0) {
                yaw += stren;
            } else if (yawDist > 0) {
                yaw -= stren;
            }
        } else {
            // aw = (float) targetRot.yaw();
        }

        pitch = mc.player.getPitch();
        if (Math.abs(pitchDist) > stren) {
            pitch = mc.player.getPitch();
            if (pitchDist < 0) {
                pitch += stren;
            } else if (pitchDist > 0) {
                pitch -= stren;
            }
        } else {
            // pitch = (float) targetRot.pitch();
        }

        if(getSetting(4).asToggle().getState()) mc.player.setYaw(yaw);
        if(getSetting(5).asToggle().getState()) mc.player.setPitch(pitch);
    }
}