package net.rotgruengelb.landscape.client.network.receive;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.LandscapeClient;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import net.rotgruengelb.landscape.network.ZoneManagerSyncS2CPacket;

import static net.rotgruengelb.landscape.network.constant.PacketIds.S2C_ZONE_MANAGER_SYNC_PACKET_ID;

public class ModS2CReceivers {
	public static void registerModS2CReceivers() {

		LandscapeClient.C_LOGGER.debug("Registering ModS2CReceivers for " + Landscape.MOD_ID);

		// ZONE_MANAGER_SYNC_PACKET RECEIVER
		ClientPlayNetworking.registerGlobalReceiver(S2C_ZONE_MANAGER_SYNC_PACKET_ID, (client, handler, buf, responseSender) -> {

			ZoneManagerSyncS2CPacket packet = new ZoneManagerSyncS2CPacket(buf);

			client.execute(() -> {
				switch (packet.getType()) {
					case ADD:
						LandscapeClient.C_LOGGER.debug("Received ZoneManagerSyncS2CPacket ADD for " + packet.getWorld());
						AvailableZoneManagers.addClientSyncedZoneManager(packet.getManager(), packet.getWorld());
						break;
					case UPDATE:
						LandscapeClient.C_LOGGER.debug("Received ZoneManagerSyncS2CPacket UPDATE for " + packet.getWorld());
						AvailableZoneManagers.updateClientSyncedZoneManager(packet.getManager(), packet.getWorld());
						break;
					case REMOVE:
						LandscapeClient.C_LOGGER.debug("Received ZoneManagerSyncS2CPacket REMOVE for " + packet.getWorld());
						AvailableZoneManagers.removeClientSyncedZoneManager(packet.getPos(), packet.getWorld());
						break;
				}
			});
		});

		// EXPANSION: NEXT HERE
	}
}
