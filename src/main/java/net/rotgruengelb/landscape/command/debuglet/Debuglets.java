package net.rotgruengelb.landscape.command.debuglet;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.rotgruengelb.landscape.command.debuglet.zone.InZoneDebuglet;
import net.rotgruengelb.landscape.command.debuglet.zone.RuleSetsDebuglet;
import net.rotgruengelb.nixienaut.list.QuickArrayList;

import java.util.List;

public class Debuglets {

	private final static RequiredArgumentBuilder<ServerCommandSource, PosArgument> STANDARD_BLOCK_POS_ARG = CommandManager.argument("block_pos", BlockPosArgumentType.blockPos());

	public static void createDebuglets() {

		ZoneDebuglets.AVAILABLE.qAdd(ZoneDebuglets.isBlockPosInManagerZone())
				.qAdd(ZoneDebuglets.isBlockPosInAnyZone())
				.qAdd(ZoneDebuglets.showAvailableRuleSets()).qAdd(ZoneDebuglets.showRuleSetRules());
	}

	public static class ZoneDebuglets {
		private static final String PERM_PREFIX = "landscape.command.main.debug.zones.";
		private static final QuickArrayList<LiteralCommandNode<ServerCommandSource>> AVAILABLE = new QuickArrayList<>();

		public static List<LiteralCommandNode<ServerCommandSource>> debuglets() {
			return AVAILABLE;
		}

		private static LiteralCommandNode<ServerCommandSource> isBlockPosInManagerZone() {
			var node = CommandManager.literal("isBlockPosInManagerZone")
					.requires(Permissions.require(PERM_PREFIX + "isBlockPosInManagerZone", 2))
					.build();

			var mangerPosArg = CommandManager.argument("manager_pos", BlockPosArgumentType.blockPos())
					.build();
			node.addChild(mangerPosArg);

			var posArg = STANDARD_BLOCK_POS_ARG.executes(InZoneDebuglet::isBlockPosInManagerZone)
					.build();
			mangerPosArg.addChild(posArg);

			return node;
		}

		private static LiteralCommandNode<ServerCommandSource> isBlockPosInAnyZone() {
			var node = CommandManager.literal("isBlockPosInAnyZone")
					.requires(Permissions.require(PERM_PREFIX + "isBlockPosInAnyZone", 2)).build();

			var posArg = STANDARD_BLOCK_POS_ARG.executes(InZoneDebuglet::isBlockPosInAnyZone)
					.build();
			node.addChild(posArg);

			return node;
		}

		private static LiteralCommandNode<ServerCommandSource> showAvailableRuleSets() {
			return CommandManager.literal("showAvailableRuleSets")
					.requires(Permissions.require(PERM_PREFIX + "showAvailableRuleSets", 2))
					.executes(RuleSetsDebuglet::showAvailableRuleSets).build();
		}

		private static LiteralCommandNode<ServerCommandSource> showRuleSetRules() {
			var node = CommandManager.literal("showRuleSetRules")
					.requires(Permissions.require(PERM_PREFIX + "showRuleSetRules", 2)).build();

			var ruleSetArg = CommandManager.argument("ruleset_name", StringArgumentType.string())
					.executes(RuleSetsDebuglet::showRuleSetRules).build();
			node.addChild(ruleSetArg);

			return node;
		}
	}
}
