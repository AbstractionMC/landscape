package net.rotgruengelb.landscape.network.receive;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.network.UpdateZoneBlockC2SPacket;

import static net.rotgruengelb.landscape.network.constant.PacketIds.C2S_UPDATE_ZONE_BLOCK_PACKET_ID;

public class ModC2SReceivers {
	public static void registerModC2SReceivers() {

		Landscape.LOGGER.debug("Registering ModC2SReceivers for " + Landscape.MOD_ID);

		// UPDATE_ZONE_BLOCK_PACKET RECEIVER
		ServerPlayNetworking.registerGlobalReceiver(C2S_UPDATE_ZONE_BLOCK_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			if (!player.isCreativeLevelTwoOp()) {
				Landscape.LOGGER.debug("Player " + player.getName()
						.toString() + " tried to send a " + C2S_UPDATE_ZONE_BLOCK_PACKET_ID + " packet without permission!");
				return;
			}
			Landscape.LOGGER.debug("Received " + C2S_UPDATE_ZONE_BLOCK_PACKET_ID + " packet from " + player.getName()
					.toString() + "!");

			UpdateZoneBlockC2SPacket packet = new UpdateZoneBlockC2SPacket(buf);
			server.execute(() -> {
				BlockPos blockPos = packet.getPos();
				if (player.getWorld()
						.getBlockEntity(blockPos) instanceof ZoneBlockBlockEntity zoneBlock) {
					zoneBlock.setMode(packet.getMode());
					zoneBlock.setShowZones(packet.shouldShowZones());
					zoneBlock.setRuleSet(packet.getRuleSet());
					zoneBlock.setZones(packet.getZones());
					zoneBlock.setPriority(packet.getPriority());
					zoneBlock.updateContext();
					zoneBlock.markDirty();
					BlockState blockState = player.getWorld().getBlockState(blockPos);
					player.getWorld()
							.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
					Landscape.LOGGER.debug("Updated ZoneBlockBlockEntity at " + blockPos.toString() + "!");
				}
			});
		});

		// EXPANSION: NEXT HERE
	}
}
