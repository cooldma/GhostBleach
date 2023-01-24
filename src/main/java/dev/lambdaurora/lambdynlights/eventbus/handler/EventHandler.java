package dev.lambdaurora.lambdynlights.eventbus.handler;

import org.apache.logging.log4j.Logger;
import dev.lambdaurora.lambdynlights.event.Event;

public abstract class EventHandler {

	private final String id;

	public EventHandler(String id) {
		this.id = id;
	}

	public abstract boolean subscribe(Object object);

	public abstract boolean unsubscribe(Object object);

	public abstract void post(Event event, Logger logger);

	public String getId() {
		return id;
	}
}
