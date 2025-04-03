package com.fixadate.domain.tag.controller.impl;

import com.fixadate.global.dto.GeneralResponseDto;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fixadate.domain.tag.controller.TagController;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.service.TagService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tag Controller", description = "Tag Controller 입니다.")
@RestControllerWithMapping("/v1/tag")
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

	private final TagService tagService;

	@Operation(summary = "Tag 등록", description = "Tag를 등록합니다.")
	@Override
	@PostMapping()
	public GeneralResponseDto createTag(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@Valid @RequestBody TagRequest tagRequest) {
		tagService.registerTag(memberPrincipal.getMember(), tagRequest);
		return GeneralResponseDto.success("created", "");
	}

	@Operation(summary = "내 Tag 조회", description = "내 Tag들을 조회합니다.")
	@Override
	@GetMapping()
	public GeneralResponseDto findTags(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {

		List<TagResponse> tagResponses = tagService.getTagResponses(
			memberPrincipal.getMember());
		return GeneralResponseDto.success("", tagResponses);
	}

	@Operation(summary = "Tag 수정", description = "Tag를 수정합니다.")
	@Override
	@PatchMapping()
	public GeneralResponseDto updateTag(
		@Valid @RequestBody TagUpdateRequest tagUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		TagResponse tagResponse = tagService.updateTag(tagUpdateRequest,
			memberPrincipal.getMember());
		return GeneralResponseDto.success("", tagResponse);
	}

	@Operation(summary = "Tag 삭제", description = "이름 기반으로 Tag를 삭제합니다.")
	@Override
	@DeleteMapping("/{name}")
	public GeneralResponseDto removeTag(@PathVariable String name,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		tagService.removeColor(name, memberPrincipal.getMember());
		return GeneralResponseDto.success("", "");
	}
}
