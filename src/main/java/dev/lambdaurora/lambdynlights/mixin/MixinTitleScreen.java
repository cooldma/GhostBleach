/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

	@Unique
	private static boolean firstLoad = true;

	private MixinTitleScreen(Text title) {
		super(title);
	}
}

//	@Inject(method = "init()V", at = @At("HEAD"))
//	private void init(CallbackInfo info) {
//		if (firstLoad) {
//			if (Option.GENERAL_SHOW_UPDATE_SCREEN.getValue()) {
//				JsonObject updateJson = LambDynLights.getUpdateJson();
//				if (updateJson != null && updateJson.has("version") && updateJson.get("version").getAsInt() > LambDynLights.INTVERSION)
//					client.setScreen(new UpdateScreen(null, updateJson));
//			}
//
//			firstLoad = false;
//			return;
//		}

//		if (BleachTitleScreen.customTitleScreen) {
//			MinecraftClient.getInstance().setScreen(
//					new WindowManagerScreen(
//							Triple.of(new BleachTitleScreen(), "LambDynLights", new ItemStack(Items.MUSIC_DISC_CAT)),
//							Triple.of(new AccountManagerScreen(), "Accounts", new ItemStack(Items.PAPER)),
//							Triple.of(ModuleClickGuiScreen.INSTANCE, "ClickGui", new ItemStack(Items.TOTEM_OF_UNDYING)),
//							Triple.of(new BleachOptionsScreen(null), "Options", new ItemStack(Items.REDSTONE)),
//							Triple.of(new BleachCreditsScreen(), "Credits", new ItemStack(Items.DRAGON_HEAD))) {
//
//						public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//							if (keyCode == ModuleManager.getModule(ClickGui.class).getKey()) {
//								selectWindow(2);
//							}
//
//							return super.keyPressed(keyCode, scanCode, modifiers);
//						}
//					});
//		} else {
//			addDrawableChild(new ButtonWidget(width / 2 - 124, height / 4 + 96, 20, 20, Text.literal("BH"), button -> {
//				BleachTitleScreen.customTitleScreen = !BleachTitleScreen.customTitleScreen;
//				FileHelper.saveMiscSetting("customTitleScreen", new JsonPrimitive(true));
//				client.setScreen(new TitleScreen(false));
//			}));
//		}
//	}
//	}
//}
