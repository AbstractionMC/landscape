package net.rotgruengelb.landscape.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.rotgruengelb.landscape.feature.zones.API;
import net.rotgruengelb.landscape.util.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.rotgruengelb.landscape.Landscape.DEV_ENV;
import static net.rotgruengelb.landscape.Landscape.LOGGER;
import static net.rotgruengelb.landscape.util.Debug.timeEnd;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void rule__core_block_place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (context.getPlayer() == null) { return; }
		long timeStart = Debug.timeStart(DEV_ENV);
		if (API.denysActionAtPos(context.getBlockPos(), "rule.core.player.block.place", context.getWorld(), Registries.ITEM.getId(context.getStack()
				.getItem()).toString())) {
			cir.setReturnValue(net.minecraft.util.ActionResult.FAIL);
		}
		timeEnd(timeStart, DEV_ENV, LOGGER, "Block place: at pos: " + context.getBlockPos()
				.toString());
	}
}
