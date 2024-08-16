package com.fixadate.global.util;

import java.util.Random;

public class RandomUtil {

	private static final Random RANDOM = new Random();

	private RandomUtil() {
	}

	public static Random getRandom() {
		return RANDOM;
	}

	public static int getRandomInt(final int bound) {
		return RANDOM.nextInt(bound);
	}
}
