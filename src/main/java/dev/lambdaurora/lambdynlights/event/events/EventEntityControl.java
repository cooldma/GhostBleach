package dev.lambdaurora.lambdynlights.event.events;

import dev.lambdaurora.lambdynlights.event.Event;

public class EventEntityControl extends Event {
	
	private Boolean canBeControlled;

	public Boolean canBeControlled() {
		return canBeControlled;
	}

	public void setControllable(Boolean canBeControlled) {
		this.canBeControlled = canBeControlled;
	}
}
