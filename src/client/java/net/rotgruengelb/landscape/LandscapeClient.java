package net.rotgruengelb.landscape;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.rotgruengelb.landscape.client.render.block.entity.ZoneBlockBlockEntityRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.rotgruengelb.landscape.block.entity.ModBlockEntities.ZONE_BLOCK_BLOCK_ENTITY;

@Environment(EnvType.CLIENT)
public class LandscapeClient implements ClientModInitializer {

	public static final Logger C_LOGGER = LoggerFactory.getLogger("Landscape/CLIENT");

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ZONE_BLOCK_BLOCK_ENTITY, ZoneBlockBlockEntityRenderer::new);
	}
}
