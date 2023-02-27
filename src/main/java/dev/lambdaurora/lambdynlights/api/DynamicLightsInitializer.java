package dev.lambdaurora.lambdynlights.api;

import java.util.function.UnaryOperator;

import com.google.gson.JsonElement;

public abstract class DynamicLightsInitializer<T> {

	private String name;
	private String tooltip;

	private T value;
	protected final T defaultValue;

	private DynamicLightHandlers<T> handler;

	public DynamicLightsInitializer(String name, String tooltip, T value, DynamicLightHandlers<T> handler) {
		this(name, tooltip, value, UnaryOperator.identity(), handler);
	}
	
	public DynamicLightsInitializer(String name, String tooltip, T value, UnaryOperator<T> defaultValue, DynamicLightHandlers<T> handler) {
		this.name = name;
		this.tooltip = tooltip;
		this.value = value;
		this.defaultValue = defaultValue.apply(value);
		this.handler = handler;
	}

	public DynamicLightHandlers<T> getHandler() {
		return this.handler;
	}

	public String getName() {
		return name;
	}

	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void resetValue() {
		setValue(defaultValue);
	}

	public boolean isDefault() {
		return getValue().equals(defaultValue);
	}
	
	public JsonElement write() {
		return getHandler().write(getValue());
	}
	
	public void read(JsonElement json) {
		T val = getHandler().readOrNull(json);
		if (val != null)
			setValue(val);
	}
}
