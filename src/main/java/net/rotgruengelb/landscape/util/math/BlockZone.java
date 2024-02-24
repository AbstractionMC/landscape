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

	public BlockZone(BlockPos pos1, BlockPos pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
	}

	public static BlockZone fromNbt(NbtElement nbt) {
		NbtList nbtList = (NbtList) nbt;
		BlockPos pos1 = blockPosFromNbtIntArray((NbtIntArray) nbtList.get(0));
		BlockPos pos2 = blockPosFromNbtIntArray((NbtIntArray) nbtList.get(1));
		return new BlockZone(pos1, pos2);
	}

	private static BlockPos blockPosFromNbtIntArray(NbtIntArray nbtIntArray) {
		int[] intArray = nbtIntArray.getIntArray();
		return new BlockPos(intArray[0], intArray[1], intArray[2]);
	}

	public static List<Integer> blockPosToList(BlockPos pos) {
		List<Integer> list = new ArrayList<>();
		list.add(pos.getX());
		list.add(pos.getY());
		list.add(pos.getZ());
		return list;
	}

	public BlockPos pos1() {
		return pos1(false);
	}

	public BlockPos pos2() {
		return pos2(false);
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
		return pos.getX() >= Math.min(this.pos1.getX(), this.pos2.getX()) &&
				pos.getX() <= Math.max(this.pos1.getX(), this.pos2.getX()) &&
				pos.getY() >= Math.min(this.pos1.getY(), this.pos2.getY()) &&
				pos.getY() <= Math.max(this.pos1.getY(), this.pos2.getY()) &&
				pos.getZ() >= Math.min(this.pos1.getZ(), this.pos2.getZ()) &&
				pos.getZ() <= Math.max(this.pos1.getZ(), this.pos2.getZ());
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
		if (direction == Direction.NORTH) {
			return this;
		}
		BlockPos rotatedPos1 = PositionUtils.rotateBlockPos(this.pos1, direction);
		BlockPos rotatedPos2 = PositionUtils.rotateBlockPos(this.pos2, direction);
		return new BlockZone(rotatedPos1, rotatedPos2);
	}
}
