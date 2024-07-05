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

	public static void main(String[] args) {
		String profileImgWithUUID = "profileImg123-123e4567-e89b-12d3-a456-426614174000";
		String cleanedProfileImg = removeUUID(profileImgWithUUID);
		System.out.println("Original: " + profileImgWithUUID);
		System.out.println("Cleaned: " + cleanedProfileImg);
	}
}
