package org.lithium.module.mods;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;
import org.lithium.module.ModuleManager;
import org.lithium.setting.module.SettingSlider;
import net.minecraft.util.math.Box;
import org.lithium.util.world.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

public class HitBoxes extends Module {

    public HitBoxes() {
        super("HitBoxes", KEY_UNBOUND, ModuleCategory.COMBAT, "Expands a player's hitbox",
                new SettingSlider("Size", 0, 10, 0.5, 2).withDesc("How much the hit box is expanded."));
    }

    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);
    }

    public void onDisable(boolean inWorld) {
        super.onDisable(inWorld);
    }



}