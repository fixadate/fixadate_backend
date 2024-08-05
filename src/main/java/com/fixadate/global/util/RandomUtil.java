package com.fixadate.global.util;

import java.util.Random;

public class RandomUtil {
	private static final Random RANDOM = new Random();

	private RandomUtil() {
	}

	public static Random current() {
		return RANDOM;
	}
}
