package net.rotgruengelb.landscape.feature.zones;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rotgruengelb.landscape.feature.zones.manager.AvailableZoneManagers;

import java.util.HashMap;
import java.util.Map;

import static net.rotgruengelb.landscape.util.Util.mapWins;

public class API {

	public static boolean posAllowsAction(BlockPos pos, String rule, World world) {
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
		if (values.isEmpty()) { return true; }
		return mapWins(values);
	}
}
