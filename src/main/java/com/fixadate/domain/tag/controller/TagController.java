package com.fixadate.domain.tag.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.global.jwt.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TagController", description = "TagController 입니다.")
public interface TagController {

	@Operation(summary = "색상 유형 생성", description = "색상 유형을 생성합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@RequestBody(description = "tagRequest",
		content = @Content(schema = @Schema(implementation = TagRequest.class)))
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "created",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "400", description = "name이 이미 존재할 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	public ResponseEntity<Void> createTag(MemberPrincipal memberPrincipal, TagRequest tagRequest);

	@Operation(summary = "색상 유형 조회", description = "사용자의 색상 유형을 조회합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = TagResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	public ResponseEntity<List<TagResponse>> findTags(MemberPrincipal memberPrincipal);

	@Operation(summary = "색상 유형 업데이트", description = "색상 유형을 업데이트합니다.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@RequestBody(description = "color, 변경할 color, 변경할 이름", required = true,
		content = @Content(schema = @Schema(implementation = TagUpdateRequest.class)))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "ok",
			content = @Content(schema = @Schema(implementation = TagResponse.class))),
		@ApiResponse(responseCode = "400", description = "새로운 컬러가 이미 존재할 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "변경하고자 하는 color가 조회되지 않을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<TagResponse> updateTag(TagUpdateRequest tagUpdateRequest,
		MemberPrincipal memberPrincipal);

	@Operation(summary = "색상 유형 삭제", description = "색상 유형을 삭제.")
	@Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
	@Parameter(name = "color", in = ParameterIn.QUERY)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "no content",
			content = @Content(schema = @Schema(implementation = TagResponse.class))),
		@ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "404", description = "삭제하고자 하는 color가 조회되지 않을 때 생기는 예외",
			content = @Content(schema = @Schema(implementation = Void.class)))
	})
	ResponseEntity<Void> removeTag(@RequestParam String name, MemberPrincipal memberPrincipal);
}
