package com.fixadate.domain.tag.dto;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagRequest;

public record TagRegisterDto(TagRequest tagRequest, Member member) {
}
