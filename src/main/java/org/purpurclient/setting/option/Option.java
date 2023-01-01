package org.purpurclient.setting.option;

import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.purpurclient.PurpurClient;
import org.purpurclient.gui.window.widget.WindowWidget;
import org.purpurclient.setting.Setting;
import org.purpurclient.setting.SettingDataHandler;
import org.purpurclient.util.BleachPlayerManager;
import org.purpurclient.util.io.BleachFileHelper;

import net.minecraft.client.MinecraftClient;

public abstract class Option<T> extends Setting<T> {

	public static Option<Boolean> GENERAL_CHECK_FOR_UPDATES = new OptionBoolean("Check For Updates", "Checks for PurpurClient updates on startup.", true);
	public static Option<Boolean> GENERAL_SHOW_UPDATE_SCREEN = new OptionBoolean("Show Update Screen", "Automatically shows the update screen on startup if an update is found.", true);

	public static Option<Boolean> PLAYERLIST_SHOW_FRIENDS = new OptionBoolean("Highlight Friends", "Highlights friends in aqua on the playerlist.", true);
	public static Option<Boolean> PLAYERLIST_SHOW_BH_USERS = new OptionBoolean("Show BH Users", "Shows other PurpurClient players on the playerlist.", true);
	public static Option<Boolean> PLAYERLIST_SHOW_AS_BH_USER = new OptionBoolean("Appear As BH User", "Makes you show up as a PurpurClient user to others.", true, b -> {
		String uuid = BleachPlayerManager.toProperUUID(MinecraftClient.getInstance().getSession().getUuid());
		if (b) {
			PurpurClient.playerMang.getPlayers().add(UUID.fromString(uuid));
			PurpurClient.playerMang.startPinger();
		} else {
			PurpurClient.playerMang.getPlayers().remove(UUID.fromString(uuid));
			PurpurClient.playerMang.stopPinger();
		}
	});

	public static Option<String> CHAT_COMMAND_PREFIX = new OptionString("Command Prefix", "The PurpurClient command prefix.", "$", s -> !s.isEmpty());
	public static Option<Boolean> CHAT_SHOW_SUGGESTIONS = new OptionBoolean("Show Suggestions", "Shows command suggestions when typing a PurpurClient command.", true);
	public static Option<Boolean> CHAT_QUICK_PREFIX = new OptionBoolean("Enable Quick Prefix", "Automatically opens chat with the PurpurClient prefix when pressing that key.", false);

	public static final Option<?>[] OPTIONS = Stream.of(Option.class.getDeclaredFields())
			.filter(f -> Option.class.isAssignableFrom(f.getType()))
			.map(ReflectionUtil::getStaticFieldValue)
			.toArray(Option[]::new);

	public Option(String name, String tooltip, T value, SettingDataHandler<T> handler) {
		super(name, tooltip, value, handler);
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);

		BleachFileHelper.SCHEDULE_SAVE_OPTIONS.set(true);
	}

	public abstract WindowWidget getWidget(int x, int y, int width, int height);
}
