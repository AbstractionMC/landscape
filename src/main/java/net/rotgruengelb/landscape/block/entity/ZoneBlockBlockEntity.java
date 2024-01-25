package net.rotgruengelb.landscape.block.entity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.accessor.LandscapeClientPlayerEntity;
import net.rotgruengelb.landscape.block.ModBlocks;
import net.rotgruengelb.landscape.block.ZoneBlock;
import net.rotgruengelb.landscape.block.enums.ZoneBlockMode;
import net.rotgruengelb.landscape.feature.zones.ZoneManager;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;
import net.rotgruengelb.landscape.util.math.BlockZone;
import net.rotgruengelb.landscape.util.math.PositionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ZoneBlockBlockEntity extends BlockEntity implements BlockEntityProvider, ZoneManager {

	private List<BlockZone> zones = new ArrayList<>();
	private ZoneBlockMode mode;
	private boolean showZones;
	private boolean powered;
	private int priority = 1;
	private String ruleSet = "landscape:rulesets/test";

	public ZoneBlockBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ZONE_BLOCK_BLOCK_ENTITY, pos, state);
		this.mode = state.get(ZoneBlock.MODE);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putBoolean("showZones", this.showZones);
		nbt.putInt("priority", this.priority);
		nbt.put("zones", this.zonesToNbt());
		nbt.putString("mode", this.mode.toString());
	}

	private NbtElement zonesToNbt() {
		NbtCompound nbt = new NbtCompound();
		if (this.zones != null) {
			for (int i = 0; i < this.zones.size(); i++) {
				nbt.put(String.valueOf(i), this.zones.get(i).toNbt());
			}
		}
		return nbt;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.priority = nbt.getInt("priority");
		this.showZones = nbt.getBoolean("showZones");
		this.zones = this.nbtToZones(nbt.getCompound("zones"));
		try {
			this.mode = ZoneBlockMode.valueOf(nbt.getString("mode").toUpperCase());
		} catch (IllegalArgumentException e) {
			this.mode = ZoneBlockMode.TRIGGER;
		}
		this.updateBlockMode();
	}

	private void updateBlockMode() {
		if (this.world != null) {
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isOf(ModBlocks.ZONE_BLOCK)) {
				this.world.setBlockState(blockPos, blockState.with(ZoneBlock.MODE, this.mode), 2);
				this.setCachedState(blockState.with(ZoneBlock.MODE, this.mode));
			}
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		if (!world.isClient) {
			AvailableZoneManagers.onCreatedManager(this.pos, world);
		}
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		if (this.world != null && !this.world.isClient) {
			AvailableZoneManagers.onRemovedManager(this.pos, this.world);
		}
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		if (this.world != null && !this.world.isClient) {
			AvailableZoneManagers.onCreatedManager(this.pos, this.world);
		}
	}

	private List<BlockZone> nbtToZones(NbtCompound zones) {
		List<BlockZone> zonesList = new ArrayList<>();
		for (String key : zones.getKeys()) {
			zonesList.add(BlockZone.fromNbt(zones.get(key)));
		}
		return zonesList;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ZoneBlockBlockEntity(pos, state);
	}

	public boolean isBlockPosInZone(BlockPos pos, boolean isRelative) {
		if (!isRelative) {
			pos = PositionUtils.blockPosToRelative(pos, this.getPos());
		}
		for (BlockZone zone : this.getZones(false)) {
			if (zone.isBlockPosInZone(pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPriority() { return priority; }

	public void setPriority(int priority) { this.priority = priority; }

	@Override
	public RuleSet getRuleSet() { return RuleSet.of(ruleSet); }

	public void setRuleSet(String ruleSet) { this.ruleSet = ruleSet; }

	public void setRuleSet(RuleSet ruleSet) { this.ruleSet = ruleSet.getIdentifier().toString(); }

	public String getRuleSetString() { return ruleSet; }

	public boolean openScreen(PlayerEntity player) {
		if (!player.isCreativeLevelTwoOp()) { return false; }
		if (player.getEntityWorld().isClient) {
			((LandscapeClientPlayerEntity) player).landscape$openZoneBlockScreen(this);
		}
		return true;
	}

	@Override
	public void markDirty() {
		if (world != null) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
		super.markDirty();
	}

	public boolean isPowered() { return this.powered; }

	public void setPowered(boolean powered) { this.powered = powered; }

	@Override
	public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

	public ZoneBlockMode getMode() { return this.mode; }

	public void setMode(ZoneBlockMode mode) {
		this.mode = mode;
		if (this.world != null) {
			BlockState blockState = this.world.getBlockState(this.getPos());
			if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
				this.world.setBlockState(this.getPos(), blockState.with(ZoneBlock.MODE, mode), 2);
			}
		}
	}

	public List<BlockZone> getZones(boolean wantRaw) {
		if (wantRaw) return this.zones;
		List<BlockZone> rotatedZones = new ArrayList<>();
		for (BlockZone zone : this.zones) {
			rotatedZones.add(zone.getWithRotation(this.getCachedState().get(ZoneBlock.FACING)));
		}
		return rotatedZones;
	}

	public List<BlockZone> getZones() { return getZones(true); }

	public void setZones(NbtCompound zones) { this.zones = this.nbtToZones(zones); }

	public boolean shouldShowZones() { return this.showZones; }

	public void setShowZones(boolean shouldShowZones) { this.showZones = shouldShowZones; }
}
