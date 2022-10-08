package io.github.friedkeenan.frigid_landing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class FrigidLandingMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("frigid_landing");

    @Override
    public void onInitialize() {
        LOGGER.info("frigid_landing initialized!");
    }
}
