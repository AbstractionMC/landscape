package net.rotgruengelb.landscape.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.feature.zones.API;
import net.rotgruengelb.landscape.util.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.rotgruengelb.landscape.Landscape.DEV_ENV;
import static net.rotgruengelb.landscape.Landscape.LOGGER;
import static net.rotgruengelb.landscape.util.Debug.timeEnd;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	/* IGNORE */
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(
			method = "isBlockBreakingRestricted", at = @At(value = "HEAD"), cancellable = true
	)
	private void rule__core_block_break(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
		long timeStart = Debug.timeStart(DEV_ENV);
		if (API.denysActionAtPos(pos, "rule.core.player.block.break", world, Registries.BLOCK.getId(world.getBlockState(pos)
				.getBlock()).toString())) {
			cir.setReturnValue(true);
		}
		timeEnd(timeStart, DEV_ENV, LOGGER, "Block break: at pos: " + pos.toString());
	}
}
