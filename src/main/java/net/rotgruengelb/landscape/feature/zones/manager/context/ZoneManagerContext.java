package net.rotgruengelb.landscape.feature.zones.manager.context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;
import net.rotgruengelb.landscape.util.math.BlockZone;
import net.rotgruengelb.landscape.util.math.PositionUtils;

import java.util.ArrayList;
import java.util.List;

public record ZoneManagerContext(BlockPos pos,
								 Direction facing,
								 RuleSet ruleSet,
								 int priority,
								 List<BlockZone> zones) {

	public static List<BlockZone> zonesFromJson(String jsonString) {
		List<BlockZone> zones = new ArrayList<>();
		JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
		JsonArray zoneArray = jsonObject.getAsJsonArray("zones");
		for (int i = 0; i < zoneArray.size(); i += 2) {
			JsonArray pos1Array = zoneArray.get(i).getAsJsonArray();
			JsonArray pos2Array = zoneArray.get(i + 1).getAsJsonArray();

			BlockPos pos1 = new BlockPos(pos1Array.get(0).getAsInt(), pos1Array.get(1)
					.getAsInt(), pos1Array.get(2).getAsInt());
			BlockPos pos2 = new BlockPos(pos2Array.get(0).getAsInt(), pos2Array.get(1)
					.getAsInt(), pos2Array.get(2).getAsInt());

			BlockZone zone = new BlockZone(pos1, pos2);
			zones.add(zone);
		}

		return zones;
	}

	public boolean isBlockPosInZone(BlockPos pos, boolean isRelative) {
		if (!isRelative) {
			pos = PositionUtils.blockPosToRelative(pos, this.pos());
		}
		for (BlockZone zone : this.getZones(false)) {
			if (zone.isBlockPosInZone(pos)) {
				return true;
			}
		}
		return false;
	}

	public List<BlockZone> getZones(boolean wantRaw) {
		if (wantRaw) return this.zones;
		List<BlockZone> rotatedZones = new ArrayList<>();
		for (BlockZone zone : this.zones) {
			rotatedZones.add(zone.getWithRotation(facing));
		}
		return rotatedZones;
	}

	@Override
	public List<BlockZone> zones() { return getZones(true); }

	public String getZonesAsJson() {
		JsonArray zoneArray = new JsonArray();
		for (BlockZone zone : this.zones) {
			JsonArray pos1Array = new JsonArray();
			pos1Array.add(zone.pos1().getX());
			pos1Array.add(zone.pos1().getY());
			pos1Array.add(zone.pos1().getZ());
			zoneArray.add(pos1Array);
			JsonArray pos2Array = new JsonArray();
			pos2Array.add(zone.pos2().getX());
			pos2Array.add(zone.pos2().getY());
			pos2Array.add(zone.pos2().getZ());
			zoneArray.add(pos2Array);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("zones", zoneArray);
		return jsonObject.toString();
	}
}
