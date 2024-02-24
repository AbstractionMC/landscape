package net.rotgruengelb.landscape.block.entity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.accessor.LandscapeClientPlayerEntity;
import net.rotgruengelb.landscape.block.ModBlocks;
import net.rotgruengelb.landscape.block.ZoneBlock;
import net.rotgruengelb.landscape.block.enums.ZoneBlockMode;
import net.rotgruengelb.landscape.feature.zones.ZoneManager;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import net.rotgruengelb.landscape.feature.zones.manager.context.ZoneManagerContext;
import net.rotgruengelb.landscape.feature.zones.rule.AvailableRuleSets;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;
import net.rotgruengelb.landscape.util.math.BlockZone;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ZoneBlockBlockEntity extends BlockEntity implements BlockEntityProvider, ZoneManager {

	private static final HashMap<ZoneBlockMode, RuleSet> MODE_RULESETS = new HashMap<>() {{
		put(ZoneBlockMode.CUSTOM_RULESET, null);
		put(ZoneBlockMode.TRIGGER, RuleSet.of(AvailableRuleSets.EMPTY_RULESET));
		put(ZoneBlockMode.DENY_WORLD_MODIFY, RuleSet.of("landscape:rulesets/test"));
	}};
	private ZoneBlockMode mode;
	private boolean showZones = false;
	private boolean powered;
	private RuleSet ruleSet = RuleSet.of(AvailableRuleSets.EMPTY_RULESET);
	private int priority = 0;
	private List<BlockZone> zones = new ArrayList<>();

	public ZoneBlockBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ZONE_BLOCK_BLOCK_ENTITY, pos, state);
		this.mode = state.get(ZoneBlock.MODE);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putBoolean("showZones", this.showZones);
		nbt.putInt("priority", this.priority);
		nbt.putString("ruleSet", this.ruleSet.getIdentifierString());
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
		this.ruleSet = RuleSet.of(Objects.requireNonNullElse(RuleSet.of(Identifier.tryParse(nbt.getString("ruleSet")))
				.getIdentifier(), AvailableRuleSets.EMPTY_RULESET));
		this.updateBlockMode();
		this.updateContext();
	}

	private List<BlockZone> nbtToZones(NbtCompound zones) {
		List<BlockZone> zonesList = new ArrayList<>();
		for (String key : zones.getKeys()) {
			zonesList.add(BlockZone.fromNbt(zones.get(key)));
		}
		return zonesList;
	}

	private void updateBlockMode() {
		if (this.world != null) {
			Landscape.LOGGER.debug("Updating block mode");
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isOf(ModBlocks.ZONE_BLOCK)) {
				this.world.setBlockState(blockPos, blockState.with(ZoneBlock.MODE, this.mode), 2);
			}
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		if (!world.isClient) {
			Landscape.LOGGER.debug("ZoneBlockBlockEntity is added to the world. Now adding to AvailableZoneManagers.");
			AvailableZoneManagers.createZoneManager(this.getZoneManagerContext(), world);
		}
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		if (this.world != null && !world.isClient) {
			Landscape.LOGGER.debug("ZoneBlockBlockEntity is removed from the world. Now removing from AvailableZoneManagers.");
			AvailableZoneManagers.removeZoneManager(this.pos, this.world);
		}
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		if (this.world != null && !world.isClient) {
			Landscape.LOGGER.debug("ZoneBlockBlockEntity is added back to the world. Now adding to AvailableZoneManagers.");
			AvailableZoneManagers.createZoneManager(this.getZoneManagerContext(), this.world);
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ZoneBlockBlockEntity(pos, state);
	}

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

	public void setPriority(int priority) { this.priority = priority; }

	public void updateContext() {
		if (this.world != null && !this.world.isClient) {
			AvailableZoneManagers.updateZoneManager(this.getZoneManagerContext(), this.world);
		}
	}

	public boolean shouldShowZones() { return this.showZones; }

	public void setShowZones(boolean shouldShowZones) { this.showZones = shouldShowZones; }

	@Override
	public ZoneManagerContext getZoneManagerContext() {
		return new ZoneManagerContext(this.pos, this.getCachedState()
				.get(ZoneBlock.FACING), this.getRuleSet(true), this.priority, this.zones);

	}

	public List<BlockZone> getZones() { return getZones(true); }

	public void setZones(NbtCompound zones) { this.zones = this.nbtToZones(zones); }

	public List<BlockZone> getZones(boolean wantRaw) {
		if (wantRaw) return this.zones;
		List<BlockZone> rotatedZones = new ArrayList<>();
		for (BlockZone zone : this.zones) {
			rotatedZones.add(zone.getWithRotation(this.getCachedState().get(ZoneBlock.FACING)));
		}
		return rotatedZones;
	}

	public RuleSet getRuleSet() {
		return this.ruleSet;
	}

	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	public RuleSet getRuleSet(boolean useMode) {
		RuleSet returnRuleSet = this.ruleSet;
		RuleSet gotRuleSet = MODE_RULESETS.get(this.mode);
		if (useMode && gotRuleSet != null) {
			returnRuleSet = gotRuleSet;
		}
		return returnRuleSet;
	}
}