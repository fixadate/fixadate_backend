package com.fixadate.domain.colortype.controller;

import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.service.ColorTypeService;
import com.fixadate.global.jwt.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/color")
public class ColorTypeControllerImpl implements ColorTypeController{

    private final ColorTypeService colorTypeService;

    @Override
    @PostMapping()
    public ResponseEntity<Void> createColorType(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                                @Valid @RequestBody ColorTypeRequest colorTypeRequest) {
        colorTypeService.insertColorType(memberPrincipal.getMember(), colorTypeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<ColorTypeResponse>> findColorTypes(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        List<ColorTypeResponse> colorTypeResponses = colorTypeService.getColorTypeResponses(memberPrincipal.getMember());
        return ResponseEntity.ok(colorTypeResponses);
    }

    @Override
    @PatchMapping()
    public ResponseEntity<ColorTypeResponse> updateColorType(@Valid @RequestBody ColorTypeUpdateRequest colorTypeUpdateRequest) {
        ColorTypeResponse colorTypeResponse = colorTypeService.updateColorType(colorTypeUpdateRequest);
        return ResponseEntity.ok(colorTypeResponse);
    }
}
