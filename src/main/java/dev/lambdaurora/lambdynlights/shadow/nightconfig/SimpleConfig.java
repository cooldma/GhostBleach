package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

public class SimpleConfig extends NightConfig {

	public SimpleConfig() {
		super("AntiChunkBan", KEY_UNBOUND, NightConfigCategory.EXPLOITS, "Bypasses chunks bans (and bookbans).");
	}

}
