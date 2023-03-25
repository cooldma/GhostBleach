/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.gui.widget.UILightSourceListWidget;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.NumberUtils;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventRenderInGameHud;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIContainer;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIWindow;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIWindow.Position;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AnnotationUtils extends NightConfig {

	private List<Text> moduleListText = new ArrayList<>();
	private Text fpsText = Text.empty();
	private Text pingText = Text.empty();
	private Text coordsText = Text.empty();
	private Text tpsText = Text.empty();
	private Text durabilityText = Text.empty();
	private Text serverText = Text.empty();
	private Text timestampText = Text.empty();

	private long prevTime = 0;
	private double tps = 20;
	private long lastPacket = 0;

	public AnnotationUtils() {
		super("UI", KEY_UNBOUND, NightConfigCategory.RENDER, false, "Shows stuff onscreen.",
				new DynamicLightsInitializerToggle("Modulelist", true).withDesc("Shows the item list.").withChildren(                                 // 0
						new DynamicLightsInitializerToggle("InnerLine", true).withDesc("Adds an extra line to the front of the item list."),
						new DynamicLightsInitializerToggle("OuterLine", false).withDesc("Adds an outer line to the item list."),
						new DynamicLightsInitializerToggle("Fill", true).withDesc("Adds a black fill behind the item list."),
						new DynamicLightsInitializerToggle("Watermark", true).withDesc("Adds the watermark to the item list.").withChildren(
								new DynamicLightsInitializerMode("Mode", "New", "Old").withDesc("The watermark type.")),
						new DynamicLightsInitializerSlider("HueBright", 0, 1, 1, 2).withDesc("The hue of the rainbow."),
						new DynamicLightsInitializerSlider("HueSat", 0, 1, 0.5, 2).withDesc("The saturation of the rainbow."),
						new DynamicLightsInitializerSlider("HueSpeed", 0.1, 50, 25, 1).withDesc("The speed of the rainbow.")),
				new DynamicLightsInitializerToggle("FPS", true).withDesc("Shows your FPS."),                                                            // 1
				new DynamicLightsInitializerToggle("Ping", true).withDesc("Shows your ping."),                                                          // 2
				new DynamicLightsInitializerToggle("Coords", true).withDesc("Shows your coords and nether coords."),                                    // 3
				new DynamicLightsInitializerToggle("TPS", true).withDesc("Shows the estimated server tps."),                                            // 4
				new DynamicLightsInitializerToggle("Durability", false).withDesc("Shows durability left on the item you're holding."),                  // 5
				new DynamicLightsInitializerToggle("Server", false).withDesc("Shows the current server you are on."),                                   // 6
				new DynamicLightsInitializerToggle("Timestamp", false).withDesc("Shows the current time.").withChildren(                                // 7
						new DynamicLightsInitializerToggle("TimeZone", true).withDesc("Shows your time zone in the time."),
						new DynamicLightsInitializerToggle("Year", false).withDesc("Shows the current year in the time.")),
				new DynamicLightsInitializerToggle("Players", false).withDesc("Lists all the players in your render distance."),                        // 8
				new DynamicLightsInitializerToggle("Armor", true).withDesc("Shows your current armor.").withChildren(                                   // 9
						new DynamicLightsInitializerToggle("Vertical", false).withDesc("Displays your armor vertically."),
						new DynamicLightsInitializerMode("Damage", "Number", "Bar", "BarV").withDesc("How to show the armor durability.")),
				new DynamicLightsInitializerToggle("Lag-Meter", true).withDesc("Shows when the server isn't responding.").withChildren(                 // 10
						new DynamicLightsInitializerMode("Animation", "Fall", "Fade", "None").withDesc("How to animate the lag meter when appearing.")),
				new DynamicLightsInitializerToggle("Inventory", false).withDesc("Renders your inventory on screen.").withChildren(                      // 11
						new DynamicLightsInitializerSlider("Background", 0, 255, 140, 0).withDesc("How opaque the background should be.")));

		UIContainer container = UILightSourceListWidget.INSTANCE.getUIContainer();

		// Modulelist
		container.windows.put("modulelist",
				new UIWindow(new Position("l", 1, "t", 2), container,
						() -> getSetting(0).asToggle().getState(),
						this::getModuleListSize,
						this::drawModuleList)
				);

		// Info
		container.windows.put("coords",
				new UIWindow(new Position("l", 1, "b", 0), container,
						() -> getSetting(3).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(coordsText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, coordsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("fps",
				new UIWindow(new Position("l", 1, "coords", 0), container,
						() -> getSetting(1).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(fpsText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, fpsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("ping",
				new UIWindow(new Position("l", 1, "fps", 0), container,
						() -> getSetting(2).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(pingText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, pingText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("tps",
				new UIWindow(new Position("l", 1, "ping", 0), container,
						() -> getSetting(4).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(tpsText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, tpsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("durability",
				new UIWindow(new Position(0.2, 0.9), container,
						() -> getSetting(5).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(durabilityText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, durabilityText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("server",
				new UIWindow(new Position(0.2, 0.85, "durability", 0), container,
						() -> getSetting(6).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(serverText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, serverText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("timestamp",
				new UIWindow(new Position(0.2, 0.8, "server", 0), container,
						() -> getSetting(7).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getWidth(timestampText) + 2, 10 },
						(ms, x, y) -> mc.textRenderer.drawWithShadow(ms, timestampText, x + 1, y + 1, 0xa0a0a0))
				);

		// Players
		container.windows.put("players",
				new UIWindow(new Position("l", 1, "modulelist", 2), container,
						() -> getSetting(8).asToggle().getState(),
						this::getPlayerSize,
						this::drawPlayerList)
				);

		// Armor
		container.windows.put("armor",
				new UIWindow(new Position(0.5, 0.85), container,
						() -> getSetting(9).asToggle().getState(),
						this::getArmorSize,
						this::drawArmor)
				);

		// Lag-Meter
		container.windows.put("lagmeter",
				new UIWindow(new Position(0, 0.05, "c", 1), container,
						() -> getSetting(10).asToggle().getState(),
						this::getLagMeterSize,
						this::drawLagMeter)
				);

		// Inventory
		container.windows.put("inventory",
				new UIWindow(new Position(0.7, 0.90), container,
						() -> getSetting(11).asToggle().getState(),
						this::getInventorySize,
						this::drawInventory)
				);
	}

	@Subscribe
	public void onTick(EventTick event) {
		// ModuleList
		moduleListText.clear();

		for (NightConfig m : NightConfigManager.getModules())
			if (m.isEnabled() && !m.isHidden())
				moduleListText.add(Text.literal(m.getName()));

		moduleListText.sort(Comparator.comparingInt(t -> -mc.textRenderer.getWidth(t)));

		if (getSetting(0).asToggle().getChild(3).asToggle().getState()) {
			int watermarkMode = getSetting(0).asToggle().getChild(3).asToggle().getChild(0).asMode().getMode();

			if (watermarkMode == 0) {
				moduleListText.add(0, LambDynLights.watermark.getText().append(Text.literal("").styled(s -> s.withColor(TextColor.fromRgb(0xf0f0f0)))));
			} else {
				String wat = String.valueOf(Math.round(Math.random() * 1000));
				moduleListText.add(0, Text.literal("\u00a7a>" + wat));
			}
		}


		// FPS
		int fps = MinecraftClient.currentFps;
		fpsText = Text.literal("FPS: ")
				.append(colorText(Integer.toString(fps), Math.min(fps, 120) / 360f));

		// Ping
		PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
		int ping = playerEntry == null ? 0 : playerEntry.getLatency();
		pingText = Text.literal("Ping: ")
				.append(colorText(Integer.toString(ping), (800 - MathHelper.clamp(ping, 0, 800)) / 2400f));

		// Coords
		boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
		BlockPos pos = mc.player.getBlockPos();
		BlockPos pos2 = nether ? new BlockPos(mc.player.getPos().multiply(8, 1, 8))
				: new BlockPos(mc.player.getPos().multiply(0.125, 1, 0.125));

		coordsText = Text.literal("XYZ: ")
				.append(Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ()).styled(s -> s.withColor(nether ? 0xb02020 : 0x40f0f0)))
				.append(" [")
				.append(Text.literal(pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()).styled(s -> s.withColor(nether ? 0x40f0f0 : 0xb02020)))
				.append("]");

		// TPS
		int time = (int) (System.currentTimeMillis() - lastPacket);
		String suffix = time >= 7500 ? "...." : time >= 5000 ? "..." : time >= 2500 ? ".." : time >= 1200 ? ".." : "";

		tpsText = Text.literal("TPS: ")
				.append(colorText(Double.toString(tps), (float) MathHelper.clamp(tps - 2, 0, 16) / 48))
				.append(suffix);

		// Durability
		ItemStack mainhand = mc.player.getMainHandStack();
		if (mainhand.isDamageable()) {
			int durability = mainhand.getOrCreateNbt().contains("dmg")
					? NumberUtils.toInt(mainhand.getOrCreateNbt().get("dmg").asString()) : mainhand.getMaxDamage() - mainhand.getDamage();

			durabilityText = Text.literal("Durability: ")
					.append(colorText(Integer.toString(durability), (float) durability / mainhand.getMaxDamage() / 3f % 1f));
		} else {
			durabilityText = Text.literal("Durability: --");
		}

		// Server
		String server = mc.getCurrentServerEntry() == null ? "Singleplayer" : mc.getCurrentServerEntry().address;
		serverText = Text.literal("Server: ")
				.append(Text.literal(server).styled(s -> s.withColor(Formatting.LIGHT_PURPLE)));

		// Timestamp
		String timeString = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd HH:mm:ss"
				+ (getSetting(7).asToggle().getChild(0).asToggle().getState() ? " zzz" : "")
				+ (getSetting(7).asToggle().getChild(1).asToggle().getState() ? " yyyy" : "")));

		timestampText = Text.literal("Time: ")
				.append(Text.literal(timeString).styled(s -> s.withColor(Formatting.YELLOW)));
	}

	@Subscribe
	public void onDrawOverlay(EventRenderInGameHud event) {
		if (mc.currentScreen instanceof UILightSourceListWidget) {
			return;
		}

		UIContainer container = UILightSourceListWidget.INSTANCE.getUIContainer();
		container.updatePositions(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
		container.render(event.getMatrix());
	}

	// --- NightConfig List

	public int[] getModuleListSize() {
		if (moduleListText.isEmpty()) {
			return new int[] { 0, 0 };
		}

		int inner = getSetting(0).asToggle().getChild(0).asToggle().getState() ? 1 : 0;
		int outer = getSetting(0).asToggle().getChild(1).asToggle().getState() ? 4 : 3;
		return new int[] { mc.textRenderer.getWidth(moduleListText.get(0)) + inner + outer, moduleListText.size() * 10 };
	}

	public void drawModuleList(MatrixStack matrices, int x, int y) {
		if (moduleListText.isEmpty()) return;

		int arrayCount = 0;
		boolean inner = getSetting(0).asToggle().getChild(0).asToggle().getState();
		boolean outer = getSetting(0).asToggle().getChild(1).asToggle().getState();
		boolean fill = getSetting(0).asToggle().getChild(2).asToggle().getState();
		boolean rightAlign = x + mc.textRenderer.getWidth(moduleListText.get(0)) / 2 > mc.getWindow().getScaledWidth() / 2;

		int startX = rightAlign ? x + mc.textRenderer.getWidth(moduleListText.get(0)) + 3 + (inner ? 1 : 0) + (outer ? 1 : 0) : x;
		for (Text t : moduleListText) {
			int color = getRainbowFromSettings(arrayCount * 40);
			int textStart = (rightAlign ? startX - mc.textRenderer.getWidth(t) - 1 : startX + 2) + (inner ? 1 : 0) * (rightAlign ? -1 : 1);
			int outerX = rightAlign ? textStart - 3 : textStart + mc.textRenderer.getWidth(t) + 1;

			if (fill) {
				DrawableHelper.fill(matrices, rightAlign ? textStart - 2 : startX, y + arrayCount * 10, rightAlign ? startX : outerX, y + 10 + arrayCount * 10, 0x70003030);
			}

			if (inner) {
				DrawableHelper.fill(matrices, rightAlign ? startX - 1 : startX, y + arrayCount * 10, rightAlign ? startX : startX + 1, y + 10 + arrayCount * 10, color);
			}

			if (outer) {
				DrawableHelper.fill(matrices, outerX, y + arrayCount * 10, outerX + 1, y + 10 + arrayCount * 10, color);
			}

			mc.textRenderer.drawWithShadow(matrices, t, textStart, y + 1 + arrayCount * 10, color);
			arrayCount++;
		}
	}

	// --- Players

	public int[] getPlayerSize() {
		List<Integer> nameLengths = mc.world.getPlayers().stream()
				.filter(e -> e != mc.player)
				.map(e -> mc.textRenderer.getWidth(
						e.getDisplayName().getString()
						+ " | "
						+ e.getBlockPos().getX() + " " + e.getBlockPos().getY() + " " + e.getBlockPos().getZ()
						+ " (" + Math.round(mc.player.distanceTo(e)) + "m)"))
				.collect(Collectors.toList());

		nameLengths.add(mc.textRenderer.getWidth("Players:"));
		nameLengths.sort(Comparator.reverseOrder());

		return new int[] { nameLengths.get(0) + 2, nameLengths.size() * 10 + 1 };
	}

	public void drawPlayerList(MatrixStack matrices, int x, int y) {
		mc.textRenderer.drawWithShadow(matrices, "Players:", x + 1, y + 1, 0xff0000);

		int count = 1;
		for (Entity e : mc.world.getPlayers().stream()
				.filter(e -> e != mc.player)
				.sorted(Comparator.comparing(mc.player::distanceTo))
				.toList()) {
			int dist = Math.round(mc.player.distanceTo(e));

			String text =
					e.getDisplayName().getString()
					+ " \u00a77|\u00a7r " +
					e.getBlockPos().getX() + " " + e.getBlockPos().getY() + " " + e.getBlockPos().getZ()
					+ " (" + dist + "m)";

			int playerColor =
					0xff000000 |
					((255 - (int) Math.min(dist * 2.1, 255) & 0xFF) << 16) |
					(((int) Math.min(dist * 4.28, 255) & 0xFF) << 8);

			mc.textRenderer.drawWithShadow(matrices, text, x + 1, y + 1 + count * 10, playerColor);
			count++;
		}
	}

	// --- Lag Meter

	public int[] getLagMeterSize() {
		return new int[] { 144, 10 };
	}

	public void drawLagMeter(MatrixStack matrices, int x, int y) {
		long time = System.currentTimeMillis();
		if (time - lastPacket > 500) {
			String text = "Server Lagging For: " + String.format(Locale.ENGLISH, "%.2f", (time - lastPacket) / 1000d) + "s";

			int xd = x + 72 - mc.textRenderer.getWidth(text) / 2;
			switch (getSetting(10).asToggle().getChild(0).asMode().getMode()) {
				case 0 -> mc.textRenderer.drawWithShadow(matrices, text, xd, y + 1 + Math.min((time - lastPacket - 1200) / 20, 0), 0xd0d0d0);
				case 1 -> mc.textRenderer.drawWithShadow(matrices, text, xd, y + 1,
						(MathHelper.clamp((int) (time - lastPacket - 500) / 3, 5, 255) << 24) | 0xd0d0d0);
				case 2 -> mc.textRenderer.drawWithShadow(matrices, text, xd, y + 1, 0xd0d0d0);
			}
		}
	}

	// --- Armor

	public int[] getArmorSize() {
		boolean vertical = getSetting(9).asToggle().getChild(0).asToggle().getState();
		return new int[] { vertical ? 18 : 74, vertical ? 62 : 16 };
	}

	public void drawArmor(MatrixStack matrices, int x, int y) {
		boolean vertical = getSetting(9).asToggle().getChild(0).asToggle().getState();

		for (int count = 0; count < mc.player.getInventory().armor.size(); count++) {
			ItemStack is = mc.player.getInventory().armor.get(count);

			if (is.isEmpty())
				continue;

			int curX = vertical ? x : x + count * 19;
			int curY = vertical ? y + 47 - count * 16 : y;
			RenderSystem.enableDepthTest();
			mc.getItemRenderer().renderGuiItemIcon(is, curX, curY);

			int durcolor = is.isDamageable() ? 0xff000000 | MathHelper.hsvToRgb((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage() / 3.0F, 1.0F, 1.0F) : 0;

			matrices.push();
			matrices.translate(0, 0, mc.getItemRenderer().zOffset + 200);
			
			if (is.getCount() > 1) {
				matrices.push();
				String s = Integer.toString(is.getCount());

				matrices.translate(curX + 19 - mc.textRenderer.getWidth(s), curY + 9, 0);
				matrices.scale(0.85f, 0.85f, 1f);

				mc.textRenderer.drawWithShadow(matrices, s, 0, 0, 0xffffff);
				matrices.pop();
			}

			if (is.isDamageable()) {
				int mode = getSetting(9).asToggle().getChild(1).asMode().getMode();
				if (mode == 0) {
					matrices.push();
					matrices.scale(0.75f, 0.75f, 1f);

					String dur = Integer.toString(is.getMaxDamage() - is.getDamage());
					mc.textRenderer.drawWithShadow(
							matrices, dur, (curX + 7 - mc.textRenderer.getWidth(dur) * 1.333f / 4) * 1.333f, (curY - (vertical ? 2 : 3)) * 1.333f, durcolor);

					matrices.pop();
				} else if (mode == 1) {
					int barLength = Math.round(13.0F - is.getDamage() * 13.0F / is.getMaxDamage());
					DrawableHelper.fill(matrices, curX + 2, curY + 13, curX + 15, curY + 15, 0xff000000);
					DrawableHelper.fill(matrices, curX + 2, curY + 13, curX + 2 + barLength, curY + 14, durcolor);
				} else {
					int barLength = Math.round(12.0F - is.getDamage() * 12.0F / is.getMaxDamage());
					DrawableHelper.fill(matrices, curX + 15, curY + 2, curX + 17, curY + 14, 0xff000000);
					DrawableHelper.fill(matrices, curX + 15, curY + 2, curX + 16, curY + 2 + barLength, durcolor);
				}
			}
			
			matrices.pop();
		}
	}

	// --- Inventory

	public int[] getInventorySize() {
		return new int[] { 155, 53 };
	}

	public void drawInventory(MatrixStack matrices, int x, int y) {
		if (getSetting(11).asToggle().getState()) {
			DrawableHelper.fill(matrices, x + 155, y, x, y + 53,
					(getSetting(11).asToggle().getChild(0).asSlider().getValueInt() << 24) | 0x212120);

			matrices.push();
			for (int i = 0; i < 27; i++) {
				ItemStack itemStack = mc.player.getInventory().getStack(i + 9);
				int offsetX = x + 1 + (i % 9) * 17;
				int offsetY = y + 1 + (i / 9) * 17;
				mc.getItemRenderer().renderGuiItemIcon(itemStack, offsetX, offsetY);
				mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, offsetX, offsetY);
			}

			mc.getItemRenderer().zOffset = 0.0F;
			RenderSystem.enableDepthTest();
			matrices.pop();
		}
	}

	@Subscribe
	public void readPacket(EventPacket.Read event) {
		lastPacket = System.currentTimeMillis();

		if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
			long time = System.currentTimeMillis();
			long timeOffset = Math.abs(1000 - (time - prevTime)) + 1000;
			tps = Math.round(MathHelper.clamp(20 / (timeOffset / 1000d), 0, 20) * 100d) / 100d;
			prevTime = time;
		}
	}

	private static Text colorText(String text, float hue) {
		return Text.literal(text).styled(s -> s.withColor(MathHelper.hsvToRgb(hue, 1f, 1f)));
	}

	public static int getRainbow(float sat, float bri, double speed, int offset) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + offset) / speed) % 360;
		return 0xff000000 | MathHelper.hsvToRgb((float) (rainbowState / 360.0), sat, bri);
	}

	public static int getRainbowFromSettings(int offset) {
		NightConfig ui = NightConfigManager.getModule(AnnotationUtils.class);

		if (ui == null)
			return getRainbow(0.5f, 0.5f, 10, 0);

		return getRainbow(
				ui.getSetting(0).asToggle().getChild(5).asSlider().getValueFloat(),
				ui.getSetting(0).asToggle().getChild(4).asSlider().getValueFloat(),
				ui.getSetting(0).asToggle().getChild(6).asSlider().getValue(),
				offset);
	}
}
