package net.rotgruengelb.landscape.feature.zones;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.rotgruengelb.landscape.Landscape.DEV_ENV;
import static net.rotgruengelb.landscape.util.Util.mapWins;

public class API {

	public static Optional<Boolean> posAllowsAction(BlockPos pos, String rule, World world, boolean includeNull) {
		long startTime = 0;
		if (DEV_ENV) {
			startTime = System.nanoTime();
		}
		Map<Integer, Boolean> values = new HashMap<>();
		for (BlockPos managerPos : AvailableZoneManagers.getManagers(world)) {
			if (world.getBlockEntity(managerPos) instanceof ZoneManager zoneManager && zoneManager.isBlockPosInZone(pos, false)) {
				if (zoneManager.getRuleSet().containsRule(rule)) {
					values.put(zoneManager.getPriority(), zoneManager.getRuleSet()
							.getRuleValue(rule));
				}
			}
		}
		if (values.isEmpty()) {
			return includeNull ? Optional.empty() : Optional.of(true);
		}

		if (DEV_ENV) {
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
			System.out.println("Check for rule: " + rule + " for pos: " + pos.toString() + " \ntook " + duration + "ms");
		}
		return Optional.of(mapWins(values));
	}

	public static boolean posAllowsAction(BlockPos pos, String rule, World world) {
		return posAllowsAction(pos, rule, world, false).orElseThrow(() -> new IllegalStateException("Result should not be null"));
	}

	public static boolean posAllowsAction(BlockPos pos, String rule, World world, String extendedRule) {
		return posAllowsAction(pos, mergeRuleParts(rule, extendedRule), world, true).orElseGet(() -> posAllowsAction(pos, rule, world));
	}

	public static String mergeRuleParts(String rule, String extendedRule) {
		return rule + "." + extendedRule;
	}
}
