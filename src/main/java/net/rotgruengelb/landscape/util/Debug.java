package net.rotgruengelb.landscape.util;

import org.slf4j.Logger;

public class Debug {
	public static long timeStart(boolean debug) {
		if (!debug) { return 0; }
		return System.nanoTime();
	}

	public static void timeEnd(long startTime, boolean debug, Logger logger, String message) {
		if (!debug) { return; }
		long endTime = System.nanoTime();
		logger.debug(String.format("%s | took %d nano seconds.", message, endTime - startTime));
	}
}
