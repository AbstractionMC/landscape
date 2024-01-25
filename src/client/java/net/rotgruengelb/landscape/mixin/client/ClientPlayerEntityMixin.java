package net.rotgruengelb.landscape.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.rotgruengelb.landscape.accessor.LandscapeClientPlayerEntity;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.client.gui.screen.ingame.ZoneBlockScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements LandscapeClientPlayerEntity {
	@Unique private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void landscape$openZoneBlockScreen(ZoneBlockBlockEntity zoneBlock) {
		this.client.setScreen(new ZoneBlockScreen(zoneBlock));
	}
}
