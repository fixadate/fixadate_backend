package com.fixadate.domain.Tag.dto.response;

import com.fixadate.domain.Tag.entity.Tag;

public record TagResponse(String color, String name, boolean isDefault) {
	public static TagResponse of(Tag tag) {
		return new TagResponse(
			tag.getColor(),
			tag.getName(),
			tag.isDefault()
		);
	}
}
