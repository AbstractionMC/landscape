package net.rotgruengelb.landscape.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.rotgruengelb.landscape.block.ModBlocks;
import net.rotgruengelb.landscape.feature.zones.API;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void place__cancelPlacementIfNotAllowed(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		context.getStack().getItem().toString();
		if (context.getStack().isOf(Item.fromBlock(ModBlocks.ZONE_BLOCK))) { return; }
		if (!API.posAllowsAction(context.getBlockPos(), "rule.core.block.place", context.getWorld())) {
			cir.setReturnValue(net.minecraft.util.ActionResult.FAIL);
		}
	}
}
