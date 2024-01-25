package net.rotgruengelb.landscape.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.integrated.IntegratedServerLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(IntegratedServerLoader.class)
public class IntegratedServerNoBackupPromptMixin {

	@ModifyVariable(
			method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lcom/mojang/serialization/Dynamic;ZZLjava/lang/Runnable;)V",
			at = @At("HEAD"),
			index = 3,
			argsOnly = true,
			ordinal = 1,
			name = "canShowBackupPrompt"
	)
	boolean start__force_canShowBackupPrompt_false(boolean b) { return false; }
}