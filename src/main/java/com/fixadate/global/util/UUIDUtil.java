package com.fixadate.global.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 5.
 */
public class UUIDUtil {
	private static final Pattern UUID_PATTERN = Pattern.compile(
		"([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})$");

	public static String removeUUID(String input) {
		Matcher matcher = UUID_PATTERN.matcher(input);
		if (matcher.find()) {
			return input.substring(0, matcher.start());
		}
		return input;
	}
}
