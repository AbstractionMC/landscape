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
		int trueCount = 0;
		int falseCount = 0;

		for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
			int priority = entry.getKey();
			boolean value = entry.getValue();

			if (priority > highestPriority) {
				highestPriority = priority;
				trueCount = value ? 1 : 0;
				falseCount = value ? 0 : 1;
			} else if (priority == highestPriority) {
				if (value) {
					trueCount++;
				} else {
					falseCount++;
				}
			}
		}

		return trueCount > falseCount;
	}
}
