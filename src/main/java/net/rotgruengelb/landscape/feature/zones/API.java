package net.rotgruengelb.landscape.feature.zones;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;
import net.rotgruengelb.landscape.feature.zones.manager.context.ZoneManagerContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static net.rotgruengelb.landscape.util.Util.mapWins;

public class API {

	public static Optional<Boolean> allowsActionAtPos(BlockPos pos, String rule, World world, boolean includeNull) {
		if (pos == null || rule == null || world == null) {
			return Optional.empty();
		}
		Landscape.LOGGER.debug("Checking rule {} at pos {}", rule, pos);

		HashMap<Integer, Boolean> values = new HashMap<>();
		ArrayList<ZoneManagerContext> zoneManagerContexts;

		zoneManagerContexts = AvailableZoneManagers.getZoneManagers(world);

		zoneManagerContexts.stream()
				.filter(zoneManagerContext -> zoneManagerContext.isBlockPosInZone(pos, false))
				.forEach(zoneManagerContext -> {
					if (zoneManagerContext.ruleSet().containsRule(rule)) {
						values.put(zoneManagerContext.priority(), zoneManagerContext.ruleSet()
								.getRuleValue(rule));
					}
				});

		Optional<Boolean> result;
		if (values.isEmpty()) {
			Landscape.LOGGER.debug("No zone found for rule {} at pos {}", rule, pos);
			result = includeNull ? Optional.empty() : Optional.of(true);
		} else {
			result = Optional.of(mapWins(values));
			Landscape.LOGGER.debug("Applying from zone found with rule {} at pos {} with result {}", rule, pos, result);
		}

		return result;
	}

	public static boolean allowsActionAtPos(@NotNull BlockPos pos, @NotNull String rule, @NotNull World world) {
		return allowsActionAtPos(pos, rule, world, false).orElseThrow(() -> new IllegalStateException("Result should not be null"));
	}

	public static boolean allowsActionAtPos(BlockPos pos, String rule, World world, String extendedRule) {
		return allowsActionAtPos(pos, mergeRuleParts(rule, extendedRule), world, true).orElseGet(() -> allowsActionAtPos(pos, rule, world));
	}

	public static boolean denysActionAtPos(BlockPos pos, String rule, World world) {
		return !allowsActionAtPos(pos, rule, world);
	}

	public static boolean denysActionAtPos(BlockPos pos, String rule, World world, String extendedRule) {
		return !allowsActionAtPos(pos, rule, world, extendedRule);
	}

	public static String mergeRuleParts(String rule, String extendedRule) {
		return rule + "." + extendedRule;
	}
}