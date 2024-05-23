package com.fixadate.domain.tag.controller.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.tag.controller.TagController;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.service.TagService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/tag")
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

	private final TagService tagService;

	@Override
	@PostMapping()
	public ResponseEntity<Void> createTag(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@Valid @RequestBody TagRequest tagRequest) {
		tagService.registTag(memberPrincipal.getMember(), tagRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Override
	@GetMapping()
	public ResponseEntity<List<TagResponse>> findTags(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {

		List<TagResponse> tagResponses = tagService.getTagResponses(
			memberPrincipal.getMember());
		return ResponseEntity.ok(tagResponses);
	}

	@Override
	@PatchMapping()
	public ResponseEntity<TagResponse> updateTag(
		@Valid @RequestBody TagUpdateRequest tagUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		TagResponse tagResponse = tagService.updateTag(tagUpdateRequest,
			memberPrincipal.getMember());
		return ResponseEntity.ok(tagResponse);
	}

	@Override
	@DeleteMapping()
	public ResponseEntity<Void> removeTag(@RequestParam String name,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		tagService.removeColor(name, memberPrincipal.getMember());
		return ResponseEntity.noContent().build();
	}
}
