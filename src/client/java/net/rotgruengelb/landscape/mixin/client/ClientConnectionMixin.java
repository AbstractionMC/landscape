package net.rotgruengelb.landscape.mixin.client;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(
			method = "disconnect", at = @At("HEAD")
	)
	private void disconnect__call_clearClientSyncedZoneManagers(Text disconnectReason, CallbackInfo ci) {
		AvailableZoneManagers.clearClientSyncedZoneManagers();
	}
}
