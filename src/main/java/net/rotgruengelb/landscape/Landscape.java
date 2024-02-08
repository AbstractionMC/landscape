package net.rotgruengelb.landscape;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.rotgruengelb.landscape.block.ModBlocks;
import net.rotgruengelb.landscape.block.entity.ModBlockEntities;
import net.rotgruengelb.landscape.command.ModCommandNodes;
import net.rotgruengelb.landscape.network.receive.ModC2SReceivers;
import net.rotgruengelb.landscape.util.resource.ModResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Landscape implements ModInitializer {

	public static final String MOD_ID = "landscape";
	public static final Logger LOGGER = LoggerFactory.getLogger("Landscape");
	public static final boolean DEV_ENV = Boolean.parseBoolean(System.getProperty("landscape.dev-env"));

	@Override
	public void onInitialize() {
		if (DEV_ENV) { LOGGER.warn("Landscape is running in development environment!"); }
		ModCommandNodes.registerModCommandNodes();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModC2SReceivers.registerModC2SReceivers();
		ModResources.registerModResourceReloadListeners();
	}
}