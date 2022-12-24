package org.lithium.eventbus.handler;

import org.apache.logging.log4j.Logger;
import org.lithium.event.Event;

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
