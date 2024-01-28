package net.rotgruengelb.landscape.feature.zones.manager;

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

	public static void onCreatedManager(BlockPos pos, World world) {
		if (world.getServer() != null) {
			getServerState(world.getServer());
		}
		Identifier worldId = world.getDimensionKey().getValue();
		MANAGERS.computeIfAbsent(worldId, k -> new ArrayList<>()).add(pos);
	}

	public static void onRemovedManager(BlockPos pos, World world) {
		if (world.getServer() != null) {
			getServerState(world.getServer());
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
