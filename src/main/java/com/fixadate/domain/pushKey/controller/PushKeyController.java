package com.fixadate.domain.pushKey.controller;

import com.fixadate.global.jwt.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "PushKeyController", description = "PushKeyController 입니다.")
public interface PushKeyController {

    @Operation(summary = "pushKey 생성", description = "PushKey를 데이터베이스에서 조회한 뒤, 있으면 업데이트 없으면 생성합니다.")
    @Parameters(value = {
            @Parameter(name = "accessToken", description = "Authorization : Bearer + <jwt>", in = ParameterIn.HEADER),
            @Parameter(name = "pushKey", description = "pushKey 값입니다.",
                    content = @Content(schema = @Schema(implementation = String.class)), in = ParameterIn.QUERY)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "jwt 만료되었을 때 생기는 예외",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "securityContext에 있는 값으로 member를 찾을 수 없는 경우",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Void> registPushKey(MemberPrincipal memberPrincipal,
                                              String pushKey);
}
