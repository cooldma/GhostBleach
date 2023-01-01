package org.purpurclient.event.events;

import org.purpurclient.event.Event;

public class EventEntityControl extends Event {
	
	private Boolean canBeControlled;

	public Boolean canBeControlled() {
		return canBeControlled;
	}

	public void setControllable(Boolean canBeControlled) {
		this.canBeControlled = canBeControlled;
	}
}
