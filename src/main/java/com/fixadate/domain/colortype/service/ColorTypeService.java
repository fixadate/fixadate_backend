package com.fixadate.domain.colortype.service;

import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorTypeService {

    private final ColorTypeRepository colorTypeRepository;

    @Transactional
    public void insertColorType(Member member, ColorTypeRequest colorTypeRequest) {
        ColorType colorType = colorTypeRequest.toEntity(member);
        colorTypeRepository.save(colorType);
    }

    public List<ColorTypeResponse> getColorTypeResponses(Member member) {
        List<ColorType> colorTypes = colorTypeRepository.findColorTypesByMember(member);
        return createColorTypeResponsesWithColorTypes(colorTypes);
    }

    private List<ColorTypeResponse> createColorTypeResponsesWithColorTypes(List<ColorType> colorTypes) {
        return colorTypes.stream()
                .map(ColorTypeResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public ColorTypeResponse updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest) {
        ColorType colorType = colorTypeRepository.findColorTypeByColor(colorTypeUpdateRequest.color())
                .orElseThrow(ColorTypeNotFoundException::new);
        colorType.updateColorType(colorTypeUpdateRequest);
        return ColorTypeResponse.of(colorType);
    }
}