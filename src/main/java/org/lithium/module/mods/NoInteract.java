package org.lithium.module.mods;

import org.lithium.event.events.EventInteract;
import org.lithium.eventbus.BleachSubscribe;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;
import org.lithium.setting.module.SettingBlockList;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/**
 * @author CUPZYY
 */

public class NoInteract extends Module {

    public NoInteract() {
        super("NoInteract", KEY_UNBOUND, ModuleCategory.PLAYER, "Prevents you from interacting with certain blocks.",
                new SettingBlockList("Edit Blocks", "Edit NoInteract Blocks",
                        Blocks.WHITE_BED,
                        Blocks.ORANGE_BED,
                        Blocks.MAGENTA_BED,
                        Blocks.LIGHT_BLUE_BED,
                        Blocks.YELLOW_BED,
                        Blocks.LIME_BED,
                        Blocks.PINK_BED,
                        Blocks.GRAY_BED,
                        Blocks.LIGHT_GRAY_BED,
                        Blocks.CYAN_BED,
                        Blocks.PURPLE_BED,
                        Blocks.BLUE_BED,
                        Blocks.BROWN_BED,
                        Blocks.GREEN_BED,
                        Blocks.RED_BED,
                        Blocks.BLACK_BED,
                        Blocks.RESPAWN_ANCHOR).withDesc("Edit the blocks to not interact with."));
    }

    @BleachSubscribe
    public void onSendPacket(EventInteract.InteractBlock event) {
        if (getSetting(0).asList(Block.class).contains(mc.world.getBlockState(event.getHitResult().getBlockPos()).getBlock())) {
        	event.setCancelled(true);
        }
    }
}
