package net.rotgruengelb.landscape.network.constant;

import net.minecraft.util.Identifier;
import net.rotgruengelb.landscape.Landscape;

public class PacketIds {

	public static final Identifier C2S_UPDATE_ZONE_BLOCK_PACKET_ID = new Identifier(Landscape.MOD_ID, "c2s_update_zone_block");
	public static final Identifier S2C_ZONE_MANAGER_SYNC_PACKET_ID = new Identifier(Landscape.MOD_ID, "s2c_zone_manager_sync");

}
