package org.lithium.event.events;

import org.lithium.event.Event;

public class EventEntityControl extends Event {
	
	private Boolean canBeControlled;

	public Boolean canBeControlled() {
		return canBeControlled;
	}

	public void setControllable(Boolean canBeControlled) {
		this.canBeControlled = canBeControlled;
	}
}
