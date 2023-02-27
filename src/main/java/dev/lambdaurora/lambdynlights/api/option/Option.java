package dev.lambdaurora.lambdynlights.api.option;

import java.util.stream.Stream;

import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import dev.lambdaurora.lambdynlights.gui.window.widget.WindowWidget;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;

public abstract class Option<T> extends DynamicLightsInitializer<T> {

	public static Option<Boolean> GENERAL_CHECK_FOR_UPDATES = new OptionBoolean("Check For Updates", "Checks for LambDynLights updates on startup.", true);
	public static Option<Boolean> GENERAL_SHOW_UPDATE_SCREEN = new OptionBoolean("Show Update Screen", "Automatically shows the update screen on startup if an update is found.", true);

	public static Option<Boolean> PLAYERLIST_SHOW_FRIENDS = new OptionBoolean("Highlight Friends", "Highlights friends in aqua on the playerlist.", true);
//	public static Option<Boolean> PLAYERLIST_SHOW_BH_USERS = new OptionBoolean("Show BH Users", "Shows other LambDynLights players on the playerlist.", true);
//	public static Option<Boolean> PLAYERLIST_SHOW_AS_BH_USER = new OptionBoolean("Appear As BH User", "Makes you show up as a LambDynLights user to others.", true, b -> {
//		String uuid = BleachPlayerManager.toProperUUID(MinecraftClient.getInstance().getSession().getUuid());
//		if (b) {
//			LambDynLights.playerMang.getPlayers().add(UUID.fromString(uuid));
//			LambDynLights.playerMang.startPinger();
//		} else {
//			LambDynLights.playerMang.getPlayers().remove(UUID.fromString(uuid));
//			LambDynLights.playerMang.stopPinger();
//		}
//	});

	public static Option<String> CHAT_COMMAND_PREFIX = new OptionString("Command Prefix", "The LambDynLights command prefix.", "$", s -> !s.isEmpty());
	public static Option<Boolean> CHAT_SHOW_SUGGESTIONS = new OptionBoolean("Show Suggestions", "Shows command suggestions when typing a LambDynLights command.", true);
	public static Option<Boolean> CHAT_QUICK_PREFIX = new OptionBoolean("Enable Quick Prefix", "Automatically opens chat with the LambDynLights prefix when pressing that key.", false);

	public static final Option<?>[] OPTIONS = Stream.of(Option.class.getDeclaredFields())
			.filter(f -> Option.class.isAssignableFrom(f.getType()))
			.map(ReflectionUtil::getStaticFieldValue)
			.toArray(Option[]::new);

	public Option(String name, String tooltip, T value, DynamicLightHandlers<T> handler) {
		super(name, tooltip, value, handler);
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);

		FileHelper.SCHEDULE_SAVE_OPTIONS.set(true);
	}

	public abstract WindowWidget getWidget(int x, int y, int width, int height);
}
