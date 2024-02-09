package net.rotgruengelb.landscape.util;

public class Debug {
	public static long timeStart() {
		return System.nanoTime();
	}

	public static long timeEnd(long startTime) {
		long endTime = System.nanoTime();
		return (endTime - startTime) / 1_000_000; // Convert to milliseconds
	}
}
