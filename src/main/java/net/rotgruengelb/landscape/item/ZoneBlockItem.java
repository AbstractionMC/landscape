package net.rotgruengelb.landscape.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.rotgruengelb.landscape.Landscape;

public class ZoneBlockItem extends BlockItem {
	public ZoneBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean canPlace(ItemPlacementContext context, BlockState state) {
		if (context.getPlayer() != null && context.getPlayer().isCreativeLevelTwoOp()) {
			Landscape.LOGGER.debug("Player " + context.getPlayer().getName().toString() + " was allowed to place a ZoneBlock because they met the required permission!");
			return super.canPlace(context, state);
		} else {
			Landscape.LOGGER.debug("Player " + context.getPlayer().getName().toString() + " tried to place a ZoneBlock without permission!");
			return false;
		}
	}
}
