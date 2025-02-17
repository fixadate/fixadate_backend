package com.fixadate.domain.tag.controller.impl;

import com.fixadate.global.dto.GeneralResponseDto;
import java.util.List;

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

@RestControllerWithMapping("/v1/tag")
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

	private final TagService tagService;

	@Override
	@PostMapping()
	public GeneralResponseDto createTag(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@Valid @RequestBody TagRequest tagRequest) {
		tagService.registerTag(memberPrincipal.getMember(), tagRequest);
		return GeneralResponseDto.success("created", "");
	}

	@Override
	@GetMapping()
	public GeneralResponseDto findTags(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {

		List<TagResponse> tagResponses = tagService.getTagResponses(
			memberPrincipal.getMember());
		return GeneralResponseDto.success("", tagResponses);
	}

	@Override
	@PatchMapping()
	public GeneralResponseDto updateTag(
		@Valid @RequestBody TagUpdateRequest tagUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		TagResponse tagResponse = tagService.updateTag(tagUpdateRequest,
			memberPrincipal.getMember());
		return GeneralResponseDto.success("", tagResponse);
	}

	@Override
	@DeleteMapping("/{name}")
	public GeneralResponseDto removeTag(@PathVariable String name,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		tagService.removeColor(name, memberPrincipal.getMember());
		return GeneralResponseDto.success("", "");
	}
}
