package dev.lambdaurora.lambdynlights.util.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.sqrt;

public class MiscUtils {

    protected static final MinecraftClient mc = MinecraftClient.getInstance();


    public static float getSpeed() {
        return (float) sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void move(float speed) {
        move(getSpeed());
    }

    public static boolean isMoving() {
//        return mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);
        return mc.player != null && (mc.options.forwardKey.isPressed());
    }

    public static boolean hasMotion() {
        return mc.player.getVelocity().x != 0.0 && mc.player.getVelocity().z != 0.0 && mc.player.getVelocity().y != 0.0;
    }

    public static void resetMotion(Boolean y) {
        mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        if (y) {
            mc.player.setVelocity(0, 0, 0);
        }
    }


    public static void strafe(float speed) {
        if (!isMoving()) {
            return;
        }
        double direction = getDirection();
        double x = -Math.sin(direction) * speed;
        double z = Math.cos(direction) * speed;

        Vec3d motion = new Vec3d(x, mc.player.getVelocity().y, z);
        mc.player.setVelocity(motion);
    }


    private static double getDirection() {
        double rotationYaw = MinecraftClient.getInstance().player.getYaw();
        if (MinecraftClient.getInstance().player.input.movementForward < 0) {
            rotationYaw += 180;
        }
        double forward = 1;
        if (MinecraftClient.getInstance().player.input.movementForward < 0) {
            forward = -0.5;
        } else if (MinecraftClient.getInstance().player.input.movementForward > 0) {
            forward = 0.5;
        }
        if (MinecraftClient.getInstance().player.input.movementSideways > 0) {
            rotationYaw -= 90 * forward;
        }
        if (MinecraftClient.getInstance().player.input.movementSideways < 0) {
            rotationYaw += 90 * forward;
        }
        return Math.toRadians(rotationYaw);
    }



//    public static double defaultSpeed() {
//        double baseSpeed = 0.2873;
//        if (mc.player.isPotionActive(Potion.moveSpeed)) {
//            int amplifier = mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
//            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
//        }
//        return baseSpeed;
//    }
}
