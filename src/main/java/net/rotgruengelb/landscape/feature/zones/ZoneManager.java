package net.rotgruengelb.landscape.feature.zones;

import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;

public interface ZoneManager {

	boolean isBlockPosInZone(BlockPos pos, boolean isRelative);

	int getPriority();

	RuleSet getRuleSet();
}
