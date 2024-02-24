package net.rotgruengelb.landscape.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.feature.zones.manager.context.ZoneManagerContext;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;
import net.rotgruengelb.landscape.util.math.BlockZone;

import java.util.List;

public class ZoneManagerSyncS2CPacket {

	private final ZoneManagerContext manager;
	private final Identifier world;
	private final int type;

	public ZoneManagerSyncS2CPacket(OperationType type, ZoneManagerContext managers, World world) {
		this.world = world.getRegistryKey().getValue();
		this.manager = managers;
		this.type = type.id;
	}

	public ZoneManagerSyncS2CPacket(PacketByteBuf buf) {
		this.type = buf.readInt();
		this.world = buf.readIdentifier();
		BlockPos pos = buf.readBlockPos();
		//		RuleSet ruleSet = RuleSet.of(buf.readIdentifier());
		RuleSet ruleSet = new RuleSet("temp debug!", buf.readIdentifier()).add("rule.core.player.block.break", false)
				.add("rule.core.player.block.place", false);
		int priority = buf.readInt();
		Direction facing = buf.readEnumConstant(Direction.class);
		List<BlockZone> zones = ZoneManagerContext.zonesFromJson(buf.readString());
		this.manager = new ZoneManagerContext(pos, facing, ruleSet, priority, zones);
	}

	public ZoneManagerContext getManager() {
		return this.manager;
	}

	public BlockPos getPos() {
		return this.manager.pos();
	}

	public Identifier getWorld() { return world; }

	public OperationType getType() { return OperationType.fromI(type); }

	public PacketByteBuf create() {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(this.type);
		buf.writeIdentifier(this.world);
		buf.writeBlockPos(this.manager.pos());
		buf.writeIdentifier(this.manager.ruleSet().getIdentifier());
		buf.writeInt(this.manager.priority());
		buf.writeEnumConstant(this.manager.facing());
		buf.writeString(this.manager.getZonesAsJson());
		return buf;
	}

	public enum OperationType {
		ADD(1),
		UPDATE(0),
		REMOVE(-1);

		public final int id;

		OperationType(int i) { this.id = i; }

		public static OperationType fromI(int i) {
			for (OperationType type : values()) {
				if (type.id == i) {
					return type;
				}
			}
			return null;
		}
	}
}


