package net.rotgruengelb.landscape.feature.zones.manager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.manager.context.ZoneManagerContext;
import net.rotgruengelb.landscape.network.ZoneManagerSyncS2CPacket;
import net.rotgruengelb.landscape.network.constant.PacketIds;

import java.util.ArrayList;
import java.util.HashMap;

public class AvailableZoneManagers {

	private final static HashMap<Identifier, HashMap<BlockPos, ZoneManagerContext>> clientSyncedZoneManagers = new HashMap<>();
	private final static HashMap<Identifier, HashMap<BlockPos, ZoneManagerContext>> zoneManagers = new HashMap<>();

	public static void createZoneManager(ZoneManagerContext manager, World world) {
		if (!world.isClient) {
			getOrCreateZoneManagers(world).put(manager.pos(), manager);
			Landscape.LOGGER.debug("Sending ZoneManagerSyncS2CPacket ADD for " + manager.pos() + " in " + world.getRegistryKey().getValue());
			world.getServer().getPlayerManager().getPlayerList()
					.forEach(player -> ServerPlayNetworking.send(player, PacketIds.S2C_ZONE_MANAGER_SYNC_PACKET_ID, new ZoneManagerSyncS2CPacket(ZoneManagerSyncS2CPacket.OperationType.ADD, manager, world).create()));
		}
	}

	@Environment(EnvType.CLIENT)
	public static void addClientSyncedZoneManager(ZoneManagerContext manager, Identifier world) {
		getOrCreateClientSyncedZoneManagers(world).put(manager.pos(), manager);
	}

	@Environment(EnvType.CLIENT)
	public static void removeClientSyncedZoneManager(BlockPos pos, Identifier world) {
		getOrCreateClientSyncedZoneManagers(world).remove(pos);
	}

	@Environment(EnvType.CLIENT)
	public static void updateClientSyncedZoneManager(ZoneManagerContext manager, Identifier world) {
		getOrCreateClientSyncedZoneManagers(world).replace(manager.pos(), manager);
	}

	@Environment(EnvType.CLIENT)
	public static void clearClientSyncedZoneManagers() {
		clientSyncedZoneManagers.clear();
	}

	@Environment(EnvType.CLIENT)
	private static HashMap<BlockPos, ZoneManagerContext> getOrCreateClientSyncedZoneManagers(Identifier world) {
		return clientSyncedZoneManagers.computeIfAbsent(world, k -> new HashMap<>());
	}

	public static void removeZoneManager(BlockPos pos, World world) {
		if (!world.isClient) {
			var allManagers = getOrCreateZoneManagers(world);
			Landscape.LOGGER.debug("Sending ZoneManagerSyncS2CPacket REMOVE for " + pos + " in " + world.getRegistryKey().getValue());
			world.getServer().getPlayerManager().getPlayerList()
					.forEach(player -> ServerPlayNetworking.send(player, PacketIds.S2C_ZONE_MANAGER_SYNC_PACKET_ID, new ZoneManagerSyncS2CPacket(ZoneManagerSyncS2CPacket.OperationType.REMOVE, allManagers.get(pos), world).create()));
			allManagers.remove(pos);
		}
	}

	public static void updateZoneManager(ZoneManagerContext manager, World world) {
		if (!world.isClient) {
			getOrCreateZoneManagers(world).replace(manager.pos(), manager);
			Landscape.LOGGER.debug("Sending ZoneManagerSyncS2CPacket UPDATE for " + manager.pos() + " in " + world.getRegistryKey().getValue());
			world.getServer().getPlayerManager().getPlayerList()
					.forEach(player -> ServerPlayNetworking.send(player, PacketIds.S2C_ZONE_MANAGER_SYNC_PACKET_ID, new ZoneManagerSyncS2CPacket(ZoneManagerSyncS2CPacket.OperationType.UPDATE, manager, world).create()));
		}
	}

	public static ArrayList<ZoneManagerContext> getZoneManagers(World world) {
		if (world.isClient) {
			return new ArrayList<>(getOrCreateClientSyncedZoneManagers(world.getRegistryKey().getValue()).values());
		}
		return new ArrayList<>(getOrCreateZoneManagers(world.getRegistryKey().getValue()).values());
	}

//	public static NbtList posListToNbtList(ArrayList<ZoneManagerContext> managers) {
//		NbtList nbtList = new NbtList();
//		managers.forEach(manager -> nbtList.add(new NbtIntArray(new int[]{manager.getPos().getX(), manager.getPos().getY(), manager.getPos().getZ()})));
//		return nbtList;
//	}
//
//	public static ArrayList<ZoneManagerContext> posNbtListToList(NbtList managers, World world) {
//		ArrayList<ZoneManagerContext> posList = new ArrayList<>();
//		managers.forEach(manager -> {
//			int[] posArray = ((NbtIntArray) manager).getIntArray();
//			BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
//			if (world.getBlockEntity(pos) instanceof ZoneManager zoneManager)
//				posList.add(zoneManager.getZoneManagerContext());
//		});
//		return posList;
//	}

	private static HashMap<BlockPos, ZoneManagerContext> getOrCreateZoneManagers(Identifier world) {
		return zoneManagers.computeIfAbsent(world, k -> new HashMap<>());
	}

	private static HashMap<BlockPos, ZoneManagerContext> getOrCreateZoneManagers(World world) {
		return getOrCreateZoneManagers(world.getRegistryKey().getValue());
	}
}