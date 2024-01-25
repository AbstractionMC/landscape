package net.rotgruengelb.landscape.util;

import java.util.Map;

public class Util {

	/**
	 * This method returns the most important boolean value from a map. <br>
	 * 1. Filters the map for the highest Integer value. <br>
	 * 2. If there are multiple Integer values with the same value, it returns the most common boolean value. <br>
	 * ... If no common boolean value is found, it returns false.
	 *
	 * @param map map with Integer as key and Boolean as value
	 * @return boolean wining boolean value
	 */
	public static boolean mapWins(Map<Integer, Boolean> map) {
		int highestPriority = 0;
		for (int priority : map.keySet()) {
			if (priority > highestPriority) {
				highestPriority = priority;
			}
		}
		int trueCount = 0;
		int falseCount = 0;
		for (int priority : map.keySet()) {
			if (priority == highestPriority) {
				if (map.get(priority)) {
					trueCount++;
				} else {
					falseCount++;
				}
			}
		}
		if (trueCount > falseCount) {
			return true;
		} else if (falseCount > trueCount) {
			return false;
		} else {
			return false;
		}
	}
}
