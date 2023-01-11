package org.purpurmc.purpur.client.event.events;

import org.purpurmc.purpur.client.event.Event;

public class EventEntityControl extends Event {
	
	private Boolean canBeControlled;

	public Boolean canBeControlled() {
		return canBeControlled;
	}

	public void setControllable(Boolean canBeControlled) {
		this.canBeControlled = canBeControlled;
	}
}
