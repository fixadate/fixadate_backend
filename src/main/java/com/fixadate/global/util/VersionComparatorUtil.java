package com.fixadate.global.util;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 11.
 */
public class VersionComparatorUtil {
	private VersionComparatorUtil() {
	}

	public static int compareVersion(String version1, String version2) {
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");

		int length = Math.max(version1Array.length, version2Array.length);
		for (int i = 0; i < length; i++) {
			Integer v1 = i < version1Array.length ? Integer.parseInt(version1Array[i]) : 0;
			Integer v2 = i < version2Array.length ? Integer.parseInt(version2Array[i]) : 0;

			int compare = v1.compareTo(v2);
			if (compare != 0) {
				return compare;
			}
		}

		return 0;
	}
}
