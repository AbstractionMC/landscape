package net.rotgruengelb.landscape.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.block.enums.ZoneBlockMode;

public class UpdateZoneBlockC2SPacket {

	public static final Identifier UPDATE_ZONE_BLOCK_PACKET_ID = new Identifier(Landscape.MOD_ID, "update_zone_block");

	private final BlockPos pos;
	private final ZoneBlockMode mode;
	private final boolean showZones;
	private final NbtCompound zones;
	private final int priority;

	public UpdateZoneBlockC2SPacket(BlockPos pos, ZoneBlockMode mode, boolean showZones, NbtCompound zones, Integer priority) {
		this.pos = pos;
		this.mode = mode;
		this.showZones = showZones;
		this.zones = zones;
		this.priority = priority;
	}

	public UpdateZoneBlockC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.mode = ZoneBlockMode.valueOf(buf.readString());
		this.showZones = buf.readBoolean();
		this.zones = buf.readNbt();
		this.priority = buf.readInt();
	}
	public int getPriority() { return priority; }

	public BlockPos getPos() { return pos; }

	public ZoneBlockMode getMode() { return mode; }

	public boolean shouldShowZones() { return showZones; }

	public NbtCompound getZones() { return zones; }

	public PacketByteBuf create() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(this.pos);
		buf.writeString(this.mode.toString());
		buf.writeBoolean(this.showZones);
		buf.writeNbt(this.zones);
		buf.writeInt(this.priority);
		return buf;
	}
}
