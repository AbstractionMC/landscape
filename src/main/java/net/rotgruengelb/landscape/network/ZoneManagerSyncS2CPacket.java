package net.rotgruengelb.landscape.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers.posListToNbtList;
import static net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers.posNbtListToList;

public class ZoneManagerSyncS2CPacket {

	private final NbtCompound pos;
	private final Identifier world;
	private final PacketType type;

	public ZoneManagerSyncS2CPacket(PacketType type, List<BlockPos> pos, World world) {
		this.world = world.getRegistryKey().getValue();
		this.pos = new NbtCompound();
		this.pos.put("pos", posListToNbtList(pos));
		this.type = type;
	}

	public ZoneManagerSyncS2CPacket(PacketByteBuf buf) {
		this.type = buf.readEnumConstant(PacketType.class);
		this.pos = buf.readNbt();
		this.world = buf.readIdentifier();
	}

	public List<BlockPos> getPos() {
		NbtList nbtList = (NbtList) pos.get("pos");
		if (nbtList == null) return new ArrayList<>();
		return posNbtListToList(nbtList);
	}

	public Identifier getWorld() { return world; }

	public PacketType getType() { return type; }

	public PacketByteBuf create() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeIdentifier(this.world);
		buf.writeNbt(this.pos);
		return buf;
	}

	public enum PacketType {
		UPDATE_ADD,
		UPDATE_REMOVE,
		INITIALIZE
	}
}


