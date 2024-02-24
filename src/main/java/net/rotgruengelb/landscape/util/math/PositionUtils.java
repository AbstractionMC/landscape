package net.rotgruengelb.landscape.util.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.rotgruengelb.landscape.Landscape;

public class PositionUtils {

	/**
	 * 90 degrees in radians
	 *
	 * @see Math#toRadians(double)
	 */
	public static final float RAD_ANG_90 = 1.5707964f;
	/**
	 * 180 degrees in radians
	 *
	 * @see Math#toRadians(double)
	 */
	public static final float RAD_ANG_180 = 3.1415927f;
	/**
	 * 270 degrees in radians
	 *
	 * @see Math#toRadians(double)
	 */
	public static final float RAD_ANG_270 = 4.712389f;

	public static BlockPos blockPosToRelative(BlockPos blockPos, BlockPos originBlockPos) {
		int x = blockPos.getX() - originBlockPos.getX();
		int y = blockPos.getY() - originBlockPos.getY();
		int z = blockPos.getZ() - originBlockPos.getZ();
		return new BlockPos(x, y, z);
	}

	/**
	 * Rotates a BlockPos around the origin (0, 0, 0) by the given direction<br>
	 * The Y coordinate will never be changed<br>
	 * If the direction is NORTH, the blockPos will be returned unchanged as it is the default direction
	 *
	 * @param blockPos  the blockPos to rotate
	 * @param direction the direction to rotate the blockPos
	 * @return the rotated blockPos
	 */
	public static BlockPos rotateBlockPos(BlockPos blockPos, Direction direction) {
		if (direction == Direction.NORTH) {
			return blockPos;
		}
		float radianAngle = switch (direction) {
			case EAST -> RAD_ANG_270;
			case SOUTH -> RAD_ANG_180;
			case WEST -> RAD_ANG_90;
			default -> {
				Landscape.LOGGER.error("Unknown direction: " + direction);
				yield 0;
			}
		};

		int x = Math.round(blockPos.getX() * MathHelper.cos(radianAngle) + blockPos.getZ() * MathHelper.sin(radianAngle));
		int z = Math.round(-blockPos.getX() * MathHelper.sin(radianAngle) + blockPos.getZ() * MathHelper.cos(radianAngle));

		return new BlockPos(x, blockPos.getY(), z);
	}
}