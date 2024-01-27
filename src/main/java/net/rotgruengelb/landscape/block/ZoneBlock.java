package net.rotgruengelb.landscape.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.block.enums.ZoneBlockMode;
import net.rotgruengelb.landscape.state.ModProperties;

public class ZoneBlock extends BlockWithEntity implements OperatorBlock, BlockEntityProvider {
	public static final EnumProperty<ZoneBlockMode> MODE = ModProperties.ZONE_BLOCK_MODE;
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final MapCodec<ZoneBlock> CODEC = ZoneBlock.createCodec(ZoneBlock::new);

	public ZoneBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(MODE, ZoneBlockMode.TRIGGER)
				.with(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ZoneBlockBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!player.isCreativeLevelTwoOp()) {
			return ActionResult.PASS;
		}
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ZoneBlockBlockEntity) {
			return ((ZoneBlockBlockEntity) blockEntity).openScreen(player) ? ActionResult.success(world.isClient) : ActionResult.PASS;
		}
		return ActionResult.PASS;
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		return super.onBreak(world, pos, state, player);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(MODE, FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) { return this.getDefaultState(); }

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!(world instanceof ServerWorld)) {
			return;
		}
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ZoneBlockBlockEntity zoneBlockBlockEntity)) {
			return;
		}
		boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos);
		boolean isPowered = zoneBlockBlockEntity.isPowered();
		if (isReceivingRedstonePower && !isPowered) {
			zoneBlockBlockEntity.setPowered(true);
		} else if (!isReceivingRedstonePower && isPowered) {
			zoneBlockBlockEntity.setPowered(false);
		}
	}
}
