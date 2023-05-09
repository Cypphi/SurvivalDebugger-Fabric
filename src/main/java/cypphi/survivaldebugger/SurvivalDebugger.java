package cypphi.survivaldebugger;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurvivalDebugger implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("SD");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Survival Debugger...");
	}
}
