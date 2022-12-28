/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium.mixin;

import org.lithium.Lithium;
import org.lithium.event.events.EventBlockBreakCooldown;
import org.lithium.event.events.EventInteract;
import org.lithium.event.events.EventReach;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

	@Shadow private int blockBreakingCooldown;

	@Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 3),
			require = 0 /* TODO: meteor compatibility */)
	private void updateBlockBreakingProgress(ClientPlayerInteractionManager clientPlayerInteractionManager, int newCooldown) {
		EventBlockBreakCooldown event = new EventBlockBreakCooldown(newCooldown);
		Lithium.eventBus.post(event);

		this.blockBreakingCooldown = event.getCooldown();
	}

	@Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 4),
			require = 0 /* TODO: meteor compatibility */)
	private void updateBlockBreakingProgress2(ClientPlayerInteractionManager clientPlayerInteractionManager, int newCooldown) {
		EventBlockBreakCooldown event = new EventBlockBreakCooldown(newCooldown);
		Lithium.eventBus.post(event);

		this.blockBreakingCooldown = event.getCooldown();
	}

	@Redirect(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I"),
			require = 0 /* TODO: meteor compatibility */)
	private void attackBlock(ClientPlayerInteractionManager clientPlayerInteractionManager, int newCooldown) {
		EventBlockBreakCooldown event = new EventBlockBreakCooldown(newCooldown);
		Lithium.eventBus.post(event);

		this.blockBreakingCooldown = event.getCooldown();
	}

	@Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
	private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
		EventInteract.BreakBlock event = new EventInteract.BreakBlock(pos);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = { "attackBlock", "updateBlockBreakingProgress" }, at = @At("HEAD"), cancellable = true)
	private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> callback) {
		EventInteract.AttackBlock event = new EventInteract.AttackBlock(pos, direction);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> callback) {
		EventInteract.InteractBlock event = new EventInteract.InteractBlock(hand, hitResult);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
	private void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callback) {
		EventInteract.InteractItem event = new EventInteract.InteractItem(hand);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
	private void getReachDistance(CallbackInfoReturnable<Float> callback) {
		EventReach event = new EventReach(callback.getReturnValueF());
		Lithium.eventBus.post(event);

		callback.setReturnValue(event.getReach());
	}
}