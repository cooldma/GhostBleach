package dev.lambdaurora.lambdynlights.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * A simple data handler that can read and write a value type to json.
 * 
 * <p>For more advanced data like children, <code>DynamicLightsInitializer.read()</code> and <code>DynamicLightsInitializer.write()</code> should be overridden.</p>
 * 
 * @param <T> the type of data to be handled
 */
public interface DynamicLightHandlers<T> {

	JsonElement write(T value);

	T read(JsonElement json) throws JsonParseException;

	default T readOrNull(JsonElement json) {
		try {
			return read(json);
		} catch (JsonParseException | IllegalStateException | UnsupportedOperationException e) {
			return null;
		}
	}
}
