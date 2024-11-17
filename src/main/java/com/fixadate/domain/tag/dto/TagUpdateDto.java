package com.fixadate.domain.tag.dto;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;

public record TagUpdateDto(TagUpdateRequest tagUpdateRequest, Member member) {
}
