package com.fixadate.domain.colortype.dto.response;

import com.fixadate.domain.colortype.entity.ColorType;

public record ColorTypeResponse(String color, String name) {
	public static ColorTypeResponse of(ColorType colorType) {
		return new ColorTypeResponse(
			colorType.getColor(),
			colorType.getName()
		);
	}
}
