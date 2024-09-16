package com.fixadate.global.util.constant;

import lombok.Getter;

@Getter
public enum ExternalCalendar {

	GOOGLE("Google Calendar", "4285F4");

	private final String tagName;
	private final String color;

	ExternalCalendar(final String tagName, final String color) {
		this.tagName = tagName;
		this.color = color;
	}
}
