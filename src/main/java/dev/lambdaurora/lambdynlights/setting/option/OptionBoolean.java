package dev.lambdaurora.lambdynlights.setting.option;

import java.util.function.Consumer;

import dev.lambdaurora.lambdynlights.gui.window.widget.WindowButtonWidget;
import dev.lambdaurora.lambdynlights.gui.window.widget.WindowWidget;
import dev.lambdaurora.lambdynlights.setting.SettingDataHandlers;

public class OptionBoolean extends Option<Boolean> {

	private Consumer<Boolean> onToggle;

	public OptionBoolean(String name, String tooltip, Boolean value) {
		super(name, tooltip, value, SettingDataHandlers.BOOLEAN);
	}

	public OptionBoolean(String name, String tooltip, Boolean value, Consumer<Boolean> onToggle) {
		this(name, tooltip, value);
		this.onToggle = onToggle;
	}

	@Override
	public WindowWidget getWidget(int x, int y, int width, int height) {
		return new WindowButtonWidget(x, y, x + width, y + height, "", () -> {
			setValue(!getValue());

			if (onToggle != null)
				onToggle.accept(getValue());
		}).withRenderEvent((w, ms, wx, wy) -> ((WindowButtonWidget) w).text = getValue() ? "\u00a7aTrue" : "\u00a7cFalse");
	}
}
