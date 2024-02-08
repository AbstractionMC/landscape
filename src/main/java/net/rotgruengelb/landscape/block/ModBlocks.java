package net.rotgruengelb.landscape.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.item.ZoneBlockItem;

public class ModBlocks {

	public static final Block ZONE_BLOCK = registerBlockNoItem("zone_block", new ZoneBlock(FabricBlockSettings.copyOf(Blocks.STRUCTURE_BLOCK)));
	public static final Item ZONE_BLOCK_ITEM = registerBlockItem("zone_block", new ZoneBlockItem(ZONE_BLOCK, new FabricItemSettings()));

	private static Block registerBlockNoItem(String name, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(Landscape.MOD_ID, name), block);
	}

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, new BlockItem(block, new FabricItemSettings()));
		return Registry.register(Registries.BLOCK, new Identifier(Landscape.MOD_ID, name), block);
	}

	private static BlockItem registerBlockItem(String name, BlockItem blockItem) {
		return Registry.register(Registries.ITEM, new Identifier(Landscape.MOD_ID, name), blockItem);
	}

	public static void registerModBlocks() {
		Landscape.LOGGER.info("Registering ModBlocks for " + Landscape.MOD_ID);
		modifyItemGroups();
	}

	private static void modifyItemGroups() {
		/* ZONE_BLOCK */
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR)
				.register(content -> content.addAfter(Items.COMMAND_BLOCK_MINECART, ZONE_BLOCK));
	}
}
