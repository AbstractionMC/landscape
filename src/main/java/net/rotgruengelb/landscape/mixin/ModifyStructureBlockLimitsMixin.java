package net.rotgruengelb.landscape.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StructureBlockBlockEntity.class)
public class ModifyStructureBlockLimitsMixin {

	@ModifyArg(
			method = "readNbt", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I",
			ordinal = 3
	), index = 2
	)
	private int readNbt__expandMax(int i) { return 100; }
}