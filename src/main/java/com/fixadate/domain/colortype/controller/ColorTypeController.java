package com.fixadate.domain.colortype.controller;

import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "ColorTypeController", description = "ColorTypeController 입니다.")
public interface ColorTypeController {

    @Operation(summary = "색상 유형 생성", description = "색상 유형을 생성합니다.")
    ResponseEntity<Void> createColorType(MemberPrincipal memberPrincipal, @Valid ColorTypeRequest colorTypeRequest);

    @Operation(summary = "색상 유형 조회", description = "사용자의 색상 유형을 조회합니다.")
    ResponseEntity<List<ColorTypeResponse>> findColorTypes(MemberPrincipal memberPrincipal);

    @Operation(summary = "색상 유형 업데이트", description = "색상 유형을 업데이트합니다.")
    ResponseEntity<ColorTypeResponse> updateColorType(@Valid ColorTypeUpdateRequest colorTypeUpdateRequest);
}
