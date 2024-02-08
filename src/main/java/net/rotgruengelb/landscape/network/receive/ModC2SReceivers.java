package net.rotgruengelb.landscape.network.receive;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.network.UpdateZoneBlockC2SPacket;

import static net.rotgruengelb.landscape.network.constant.PacketIds.C2S_UPDATE_ZONE_BLOCK_PACKET_ID;

public class ModC2SReceivers {
	public static void registerModC2SReceivers() {

		// UPDATE_ZONE_BLOCK_PACKET RECEIVER
		ServerPlayNetworking.registerGlobalReceiver(C2S_UPDATE_ZONE_BLOCK_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			if (player.isCreativeLevelTwoOp()) {
				UpdateZoneBlockC2SPacket packet = new UpdateZoneBlockC2SPacket(buf);
				server.execute(() -> {
					BlockPos blockPos = packet.getPos();
					if (player.getWorld()
							.getBlockEntity(blockPos) instanceof ZoneBlockBlockEntity blockEntity) {
						blockEntity.setMode(packet.getMode());
						blockEntity.setShowZones(packet.shouldShowZones());
						blockEntity.setZones(packet.getZones());
						blockEntity.setPriority(packet.getPriority());
						blockEntity.setRuleSet(packet.getRuleSet());
						blockEntity.markDirty();
						BlockState blockState = player.getWorld().getBlockState(blockPos);
						player.getWorld()
								.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
					}
				});
			}
		});

		// NEXT HERE
	}
}
