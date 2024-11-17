package com.fixadate.domain.tag.mapper;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.TagRegisterDto;
import com.fixadate.domain.tag.dto.TagUpdateDto;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagMapper {

	public static Tag toEntity(Member member, TagRequest tagRequest) {
		return Tag.builder()
				  .color(tagRequest.color())
				  .name(tagRequest.name())
				  .systemDefined(false)
				  .member(member)
				  .build();
	}

	public static TagResponse toResponse(Tag tag) {
		return new TagResponse(
			tag.getColor(),
			tag.getName(),
			tag.isSystemDefined()
		);
	}

	public static TagRegisterDto toRegisterDto(TagRequest tagRequest, Member member) {
		return new TagRegisterDto(
			tagRequest,
			member
		);
	}

	public static TagUpdateDto toUpdateDto(TagUpdateRequest tagUpdateRequest, Member member) {
		return new TagUpdateDto(
			tagUpdateRequest,
			member
		);
	}
}
