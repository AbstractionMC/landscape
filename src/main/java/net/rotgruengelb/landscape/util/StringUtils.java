package net.rotgruengelb.landscape.util;

public class StringUtils {
	public static String removeFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex != -1) {
			return fileName.substring(0, lastDotIndex);
		} else {
			return fileName;
		}
	}
}
