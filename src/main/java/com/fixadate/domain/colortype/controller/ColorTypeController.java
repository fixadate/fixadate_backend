package com.fixadate.domain.colortype.controller;

import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "ColorTypeController", description = "ColorTypeController 입니다.")
public interface ColorTypeController {

    @Operation(summary = "색상 유형 생성", description = "색상 유형을 생성합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @RequestBody(description = "colorTypeRequest",
            content = @Content(schema = @Schema(implementation = ColorTypeRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "color가 이미 존재할 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Void> createColorType(MemberPrincipal memberPrincipal, ColorTypeRequest colorTypeRequest);


    @Operation(summary = "색상 유형 조회", description = "사용자의 색상 유형을 조회합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = ColorTypeResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<List<ColorTypeResponse>> findColorTypes(MemberPrincipal memberPrincipal);

    @Operation(summary = "색상 유형 업데이트", description = "색상 유형을 업데이트합니다.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @RequestBody(description = "color, 변경할 color, 변경할 이름", required = true,
            content = @Content(schema = @Schema(implementation = ColorTypeUpdateRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = ColorTypeResponse.class))),
            @ApiResponse(responseCode = "400", description = "새로운 컬러가 이미 존재할 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "변경하고자 하는 color가 조회되지 않을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<ColorTypeResponse> updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest, MemberPrincipal memberPrincipal);

    @Operation(summary = "색상 유형 삭제", description = "색상 유형을 삭제.")
    @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER)
    @Parameter(name = "color", in = ParameterIn.QUERY)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "no content",
                    content = @Content(schema = @Schema(implementation = ColorTypeResponse.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "삭제하고자 하는 color가 조회되지 않을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<Void> removeColorType(@RequestParam String color, MemberPrincipal memberPrincipal);
}
