package com.fixadate.domain.colortype.controller.impl;

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

import com.fixadate.domain.colortype.controller.ColorTypeController;
import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.service.ColorTypeService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestControllerWithMapping("/color")
@RequiredArgsConstructor
public class ColorTypeControllerImpl implements ColorTypeController {

	private final ColorTypeService colorTypeService;

	@Override
	@PostMapping()
	public ResponseEntity<Void> createColorType(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
		@Valid @RequestBody ColorTypeRequest colorTypeRequest) {
		colorTypeService.registColorType(memberPrincipal.getMember(), colorTypeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Override
	@GetMapping()
	public ResponseEntity<List<ColorTypeResponse>> findColorTypes(
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {

		List<ColorTypeResponse> colorTypeResponses = colorTypeService.getColorTypeResponses(
			memberPrincipal.getMember());
		return ResponseEntity.ok(colorTypeResponses);
	}

	@Override
	@PatchMapping()
	public ResponseEntity<ColorTypeResponse> updateColorType(
		@Valid @RequestBody ColorTypeUpdateRequest colorTypeUpdateRequest,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		ColorTypeResponse colorTypeResponse = colorTypeService.updateColorType(colorTypeUpdateRequest,
			memberPrincipal.getMember());
		return ResponseEntity.ok(colorTypeResponse);
	}

	@Override
	@DeleteMapping()
	public ResponseEntity<Void> removeColorType(@RequestParam String color,
		@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
		colorTypeService.removeColor(color, memberPrincipal.getMember());
		return ResponseEntity.noContent().build();
	}
}
