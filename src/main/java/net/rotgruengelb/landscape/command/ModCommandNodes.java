package net.rotgruengelb.landscape.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.command.debuglet.Debuglets;

public class ModCommandNodes {

	public static void registerModCommandNodes() {

		Landscape.LOGGER.debug("Registering ModCommandNodes for " + Landscape.MOD_ID);

		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {

			var adventureCoreNode = CommandManager.literal("landscape")
					.requires(Permissions.require("landscape.command.main", 2)).build();
			dispatcher.getRoot().addChild(adventureCoreNode);

			var debugNode = CommandManager.literal("debug")
					.requires(Permissions.require("landscape.command.main.debug", 2)).build();
			adventureCoreNode.addChild(debugNode);

			// DEBUG: ZONES
			var debugZonesNode = CommandManager.literal("zones")
					.requires(Permissions.require("landscape.command.main.debug.zones", 2)).build();
			debugNode.addChild(debugZonesNode);
			Debuglets.createDebuglets();
			for (LiteralCommandNode<ServerCommandSource> zonesDebuglet : Debuglets.ZoneDebuglets.debuglets()) {
				debugZonesNode.addChild(zonesDebuglet);
			}
		}));
	}
}
