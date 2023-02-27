package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerBlockList;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventInteract;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/**
 * @author CUPZYY
 */

public class IndentStyle extends NightConfig {

    public IndentStyle() {
        super("NoInteract", KEY_UNBOUND, NightConfigCategory.PLAYER, "Prevents you from interacting with certain blocks.",
                new DynamicLightsInitializerBlockList("Edit Blocks", "Edit NoInteract Blocks",
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

    @Subscribe
    public void onSendPacket(EventInteract.InteractBlock event) {
        if (getSetting(0).asList(Block.class).contains(mc.world.getBlockState(event.getHitResult().getBlockPos()).getBlock())) {
        	event.setCancelled(true);
        }
    }
}
