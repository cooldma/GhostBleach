package dev.lambdaurora.lambdynlights.mixin;

import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.shadow.nightconfig.SimpleConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {

	@ModifyArg(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readNbt(Lnet/minecraft/nbt/NbtTagSizeTracker;)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtTagSizeTracker increaseLimit(NbtTagSizeTracker in) {
        return NightConfigManager.getModule(SimpleConfig.class).isEnabled() ? NbtTagSizeTracker.EMPTY : in;
    }
}
