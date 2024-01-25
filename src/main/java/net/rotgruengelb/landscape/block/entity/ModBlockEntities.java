package net.rotgruengelb.landscape.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.block.ModBlocks;

public class ModBlockEntities {
	public static void registerModBlockEntities() {
		Landscape.LOGGER.info("Registering ModBlockEntities for " + Landscape.MOD_ID);
	}

	public static final BlockEntityType<ZoneBlockBlockEntity> ZONE_BLOCK_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Registries.BLOCK.getId(ModBlocks.ZONE_BLOCK), FabricBlockEntityTypeBuilder.create(ZoneBlockBlockEntity::new, ModBlocks.ZONE_BLOCK)
			.build());
}
