package net.rotgruengelb.landscape.client.network.receive;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import static net.rotgruengelb.landscape.network.constant.PacketIds.S2C_ZONE_MANAGER_SYNC_PACKET_ID;

public class ModS2CReceivers {
	public static void registerModS2CReceivers() {

		// ZONE_MANAGER_SYNC_PACKET RECEIVER
		ClientPlayNetworking.registerGlobalReceiver(S2C_ZONE_MANAGER_SYNC_PACKET_ID, (client, handler, buf, responseSender) -> {

		});

		// NEXT HERE
	}
}
