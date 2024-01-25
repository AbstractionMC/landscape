package net.rotgruengelb.landscape.util.math;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class BlockZone {

	private final BlockPos pos1;
	private final BlockPos pos2;
	private final MXVect min;
	private final MXVect max;

	public BlockZone(BlockPos pos1, BlockPos pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.min = new MXVect(pos1, pos2, false);
		this.max = new MXVect(pos1, pos2, true);
	}

	public static BlockZone fromNbt(NbtElement nbt) {
		NbtList nbtList = (NbtList) nbt;
		BlockPos pos1 = blockPosFromNbtIntArray((NbtIntArray) nbtList.get(0));
		BlockPos pos2 = blockPosFromNbtIntArray((NbtIntArray) nbtList.get(1));
		return new BlockZone(pos1, pos2);
	}

	public static BlockPos blockPosFromNbtIntArray(NbtIntArray nbtIntArray) {
		return new BlockPos(nbtIntArray.get(0).intValue(), nbtIntArray.get(1)
				.intValue(), nbtIntArray.get(2).intValue());
	}

	public static List<Integer> blockPosToList(BlockPos pos) {
		List<Integer> list = new ArrayList<>();
		list.add(pos.getX());
		list.add(pos.getY());
		list.add(pos.getZ());
		return list;
	}

	public BlockPos pos1(boolean forRender) {
		if (!forRender) {
			return this.pos1;
		}
		BlockPos pos1 = new BlockPos(this.pos1);
		if (this.pos1.getX() >= this.pos2.getX()) {
			pos1 = pos1.add(1, 0, 0);
		}
		if (this.pos1.getY() >= this.pos2.getY()) {
			pos1 = pos1.add(0, 1, 0);
		}
		if (this.pos1.getZ() >= this.pos2.getZ()) {
			pos1 = pos1.add(0, 0, 1);
		}
		return pos1;
	}

	public BlockPos pos2(boolean forRender) {
		if (!forRender) {
			return this.pos2;
		}
		BlockPos pos2 = new BlockPos(this.pos2);
		if (this.pos2.getX() > this.pos1.getX()) {
			pos2 = pos2.add(1, 0, 0);
		}
		if (this.pos2.getY() > this.pos1.getY()) {
			pos2 = pos2.add(0, 1, 0);
		}
		if (this.pos2.getZ() > this.pos1.getZ()) {
			pos2 = pos2.add(0, 0, 1);
		}
		return pos2;
	}

	public boolean isBlockPosInZone(BlockPos pos) {

		return pos.getX() >= min.X && pos.getX() <= max.X && pos.getY() >= min.Y && pos.getY() <= max.Y && pos.getZ() >= min.Z && pos.getZ() <= max.Z;
	}

	public NbtElement toNbt() {
		NbtList nbtList = new NbtList();
		nbtList.add(new NbtIntArray(blockPosToList(this.pos1)));
		nbtList.add(new NbtIntArray(blockPosToList(this.pos2)));
		return nbtList;
	}

	@Override
	public String toString() {
		return "BlockZone{" + "pos1=" + pos1 + ", pos2=" + pos2 + "}";
	}

	public BlockZone getWithRotation(Direction direction) {
		if (!direction.equals(Direction.NORTH)) {
			BlockPos pos1 = PositionUtils.rotateBlockPos(this.pos1, direction);
			BlockPos pos2 = PositionUtils.rotateBlockPos(this.pos2, direction);
			return new BlockZone(pos1, pos2);
		}
		return new BlockZone(this.pos1, this.pos2);
	}

	private static class MXVect {
		public final double X;
		public final double Y;
		public final double Z;

		private MXVect(BlockPos pos1, BlockPos pos2, boolean isMax) {

			if (isMax) {
				this.X = Math.max(pos1.getX(), pos2.getX());
				this.Y = Math.max(pos1.getY(), pos2.getY());
				this.Z = Math.max(pos1.getZ(), pos2.getZ());
			} else {
				this.X = Math.min(pos1.getX(), pos2.getX());
				this.Y = Math.min(pos1.getY(), pos2.getY());
				this.Z = Math.min(pos1.getZ(), pos2.getZ());
			}
		}
	}
}
