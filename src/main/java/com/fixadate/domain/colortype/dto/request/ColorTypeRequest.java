package com.fixadate.domain.colortype.dto.request;

import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;

public record ColorTypeRequest(
	@NotBlank String color,
	@NotBlank String name) {

	public ColorType toEntity(Member member) {
		return ColorType.builder()
			.color(color)
			.name(name)
			.member(member)
			.build();
	}
}
