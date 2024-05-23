package com.fixadate.domain.Tag.dto.request;

import com.fixadate.domain.Tag.entity.Tag;
import com.fixadate.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;

public record TagRequest(
	@NotBlank String color,
	@NotBlank String name) {

	public Tag toEntity(Member member) {
		return Tag.builder()
			.color(color)
			.name(name)
			.isDefault(false)
			.member(member)
			.build();
	}
}
