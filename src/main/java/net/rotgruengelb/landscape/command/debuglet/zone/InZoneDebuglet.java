package net.rotgruengelb.landscape.command.debuglet.zone;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.ZoneManager;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import net.rotgruengelb.landscape.feature.zones.manager.context.ZoneManagerContext;
import org.apache.commons.lang3.StringUtils;

import static net.rotgruengelb.landscape.util.math.PositionUtils.blockPosToRelative;

public class InZoneDebuglet {

	private static final String DEFAULT_ERROR_MESSAGE = "An error occurred whilst executing: %s\nCheck the Logs for stacktrace.";

	public static int isBlockPosInAnyZone(CommandContext<ServerCommandSource> ctx) {
		try {
			BlockPos blockPos = BlockPosArgumentType.getBlockPos(ctx, "block_pos");
			ctx.getSource().sendMessage(Text.of(StringUtils.center("[LANDSCAPE DEBUG isBlockPosInAnyZone]", 50, "-")));
			for (ZoneManagerContext zoneManager : AvailableZoneManagers.getZoneManagers(ctx.getSource()
					.getWorld())) {
				if (zoneManager.isBlockPosInZone(blockPos, false)) {
					ctx.getSource()
							.sendMessage(Text.literal("Block at " + blockPos + " is in a Zone managed by the Block at " + zoneManager.pos())
									.setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
				}
			}
			ctx.getSource().sendMessage(Text.of(StringUtils.center("", 47, "-")));
			return 1;
		} catch (Exception e) {
			Landscape.LOGGER.error(e.getMessage(), e);
			ctx.getSource()
					.sendError(Text.literal(String.format(DEFAULT_ERROR_MESSAGE, e.getMessage())));
			return -1;
		}
	}

	public static int isBlockPosInManagerZone(CommandContext<ServerCommandSource> ctx) {
		try {
			BlockPos blockPos = BlockPosArgumentType.getBlockPos(ctx, "block_pos");
			BlockPos managerPos = BlockPosArgumentType.getBlockPos(ctx, "manager_pos");
			BlockPos relativeBlockPos = blockPosToRelative(blockPos, managerPos);
			ctx.getSource().sendMessage(Text.of("manager: " + blockPos + ", " + managerPos));
			BlockEntity blockEntity = ctx.getSource().getWorld().getBlockEntity(managerPos);
			ctx.getSource().sendMessage(Text.of(StringUtils.center("[LANDSCAPE DEBUG isBlockPosInManagerZone]", 50, "-")));
			if (blockEntity instanceof ZoneManager zoneManager) {
				if (zoneManager.getZoneManagerContext().isBlockPosInZone(relativeBlockPos, true)) {
					ctx.getSource()
							.sendMessage(Text.literal("Block at " + blockPos + "(As relative: " + relativeBlockPos + ") is in a Zone managed by the Block at " + managerPos)
									.setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
				} else {
					ctx.getSource()
							.sendMessage(Text.literal("Block at " + blockPos + " (As relative: " + relativeBlockPos + ") is not in a Zone managed by the Block at " + managerPos)
									.setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
				}
			} else {
				ctx.getSource()
						.sendError(Text.literal("Block at " + managerPos + " is not a valid Zone Manager Block"));
			}
			ctx.getSource().sendMessage(Text.of(StringUtils.center("", 47, "-")));
			return 1;
		} catch (Exception e) {
			Landscape.LOGGER.error(e.getMessage(), e);
			ctx.getSource()
					.sendError(Text.literal(String.format(DEFAULT_ERROR_MESSAGE, e.getMessage())));
			return -1;
		}
	}
}
