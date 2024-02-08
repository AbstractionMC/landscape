package net.rotgruengelb.landscape.feature.zones.manager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.Landscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableZoneManagers extends PersistentState {

	private static final Map<Identifier, List<BlockPos>> MANAGERS = new HashMap<>();
	private static final Type<AvailableZoneManagers> type = new Type<>(AvailableZoneManagers::new, AvailableZoneManagers::createFromNbt, null);
	@Environment(EnvType.CLIENT) private static boolean clientIsInitialized = false;

	public static void onCreatedManager(BlockPos pos, World world) {
		MinecraftServer server = world.getServer();
		if (server != null) {
			getServerState(server);
		}
		Identifier worldId = world.getDimensionKey().getValue();
		MANAGERS.computeIfAbsent(worldId, k -> new ArrayList<>()).add(pos);
	}

	@Environment(EnvType.CLIENT)
	public static void clientInitialized() {
		clientIsInitialized = true;
	}

	@Environment(EnvType.CLIENT)
	public static boolean isClientInitialized() {
		return clientIsInitialized;
	}

	public static void onRemovedManager(BlockPos pos, World world) {
		MinecraftServer server = world.getServer();
		if (server != null) {
			getServerState(server);
		}
		List<BlockPos> dimManagers = MANAGERS.get(world.getDimensionKey().getValue());
		dimManagers.remove(pos);
	}

	public static List<BlockPos> getManagers(World world) {
		if (!MANAGERS.containsKey(world.getDimensionKey().getValue())) {
			MANAGERS.put(world.getDimensionKey().getValue(), new ArrayList<>());
		}
		return MANAGERS.get(world.getDimensionKey().getValue());
	}

	private static AvailableZoneManagers createFromNbt(NbtCompound nbtCompound) {
		AvailableZoneManagers state = new AvailableZoneManagers();
		NbtCompound managers = nbtCompound.getCompound("managers");
		for (String dimension : managers.getKeys()) {
			NbtList sub_managers = managers.getList(dimension, NbtElement.INT_ARRAY_TYPE);
			List<BlockPos> dimension_managers = new ArrayList<>();
			for (NbtElement sub_manager : sub_managers) {
				int[] sub_manager_pos = ((NbtIntArray) sub_manager).getIntArray();
				dimension_managers.add(new BlockPos(sub_manager_pos[0], sub_manager_pos[1], sub_manager_pos[2]));
			}
			MANAGERS.put(new Identifier(dimension), dimension_managers);
		}
		return state;
	}

	public static AvailableZoneManagers getServerState(MinecraftServer server) {
		PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD)
				.getPersistentStateManager();
		AvailableZoneManagers state = persistentStateManager.getOrCreate(type, Landscape.MOD_ID);
		state.markDirty();
		return state;
	}

	public static NbtList posListToNbtList(List<BlockPos> managers) {
		NbtList nbtList = new NbtList();
		for (BlockPos pos : managers) {
			nbtList.add(new NbtIntArray(List.of(pos.getX(), pos.getY(), pos.getZ())));
		}
		return nbtList;
	}

	public static List<BlockPos> posNbtListToList(NbtList managers) {
		List<BlockPos> posList = new ArrayList<>();
		for (NbtElement pos : managers) {
			int[] posArray = ((NbtIntArray) pos).getIntArray();
			posList.add(new BlockPos(posArray[0], posArray[1], posArray[2]));
		}
		return posList;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound managers = new NbtCompound();
		for (Identifier dimension : MANAGERS.keySet()) {
			NbtList sub_managers = new NbtList();
			for (BlockPos pos : MANAGERS.get(dimension)) {
				sub_managers.add(new NbtIntArray(List.of(pos.getX(), pos.getY(), pos.getZ())));
			}
			managers.put(dimension.toString(), sub_managers);
		}
		nbt.put("managers", managers);
		return nbt;
	}
}
