package com.fixadate.global.util;

import java.util.Random;

public class RandomValueUtil {
	static long seed = System.currentTimeMillis();
	static Random random = new Random(seed);

	private RandomValueUtil() {

	}

	public static String createRandomString(int length) {
		var leftLimit = 65;
		var rightLimit = 122;

		return random.ints(leftLimit, rightLimit + 1)
			.filter(i -> (i <= 90 || i >= 97))
			.limit(length)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
}
