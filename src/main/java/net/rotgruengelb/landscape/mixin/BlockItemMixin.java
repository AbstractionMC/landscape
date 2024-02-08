package net.rotgruengelb.landscape.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.API;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void rule__core_block_place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		Landscape.LOGGER.info("BlockItemMixin.place");
		System.out.println("BlockItemMixin.place");
		//		if (context.getStack().isOf(ZONE_BLOCK_ITEM)) { return; } (HOPEFULLY NOT NEEDED ANYMORE)
		if (context.getPlayer() == null) { return; }
		if (!API.posAllowsAction(context.getBlockPos(), "rule.core.player.block.place", context.getWorld(), Registries.ITEM.getId(context.getStack()
				.getItem()).toString())) {
			cir.setReturnValue(net.minecraft.util.ActionResult.FAIL);
		}
	}
}
