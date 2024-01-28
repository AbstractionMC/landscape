package net.rotgruengelb.landscape.feature.zones;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.rotgruengelb.landscape.util.Util.mapWins;

public class API {

	public static Object posAllowsAction(BlockPos pos, String rule, World world, boolean includeNull) {
		Map<Integer, Boolean> values = new HashMap<>();
		for (BlockPos managerPos : AvailableZoneManagers.getManagers(world)) {
			ZoneManager zoneManager = (ZoneManager) world.getBlockEntity(managerPos);
			if (!(world.getBlockEntity(managerPos) instanceof ZoneManager) || zoneManager == null) {
				continue;
			}
			if (zoneManager.isBlockPosInZone(pos, false)) {
				if (zoneManager.getRuleSet().containsRule(rule)) {
					values.put(zoneManager.getPriority(), zoneManager.getRuleSet()
							.getRuleValue(rule));
				}
			}
		}
		if (values.isEmpty()) {
			if (includeNull) {
				return null;
			}
			return true;
		}
		return mapWins(values);
	}

	public static boolean posAllowsAction(BlockPos pos, String rule, World world) {
		@NotNull var result = Objects.requireNonNull(posAllowsAction(pos, rule, world, false));
		return (boolean) result;
	}

	public static boolean posAllowsAction(BlockPos pos, String rule, World world, String extendedRule) {
		var extendedResult = posAllowsAction(pos, mergeRuleParts(rule, extendedRule), world, true);
		if (extendedResult == null) {
			return posAllowsAction(pos, rule, world);
		}
		return (boolean) extendedResult;
	}

	public static String mergeRuleParts(String rule, String extendedRule) {
		return rule + "." + extendedRule;
	}
}
