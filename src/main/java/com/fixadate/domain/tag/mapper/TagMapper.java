package com.fixadate.domain.tag.mapper;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagMapper {

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
}
