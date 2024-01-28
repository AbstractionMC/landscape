package net.rotgruengelb.landscape.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

public class ZoneBlockItem extends BlockItem {
	public ZoneBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean canPlace(ItemPlacementContext context, BlockState state) {
		if (context.getPlayer() != null && context.getPlayer().isCreativeLevelTwoOp()) {
			return super.canPlace(context, state);
		} else {
			return false;
		}
	}
}
