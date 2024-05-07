package com.fixadate.domain.colortype.service;

import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.exception.ColorTypeDuplicatedException;
import com.fixadate.domain.colortype.exception.ColorTypeNotFoundException;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorTypeService {

    private final ColorTypeRepository colorTypeRepository;

    @Transactional
    public void insertColorType(Member member, ColorTypeRequest colorTypeRequest) {
        checkColor(colorTypeRequest.color(), member);
        ColorType colorType = colorTypeRequest.toEntity(member);
        colorTypeRepository.save(colorType);
    }

    @Transactional(readOnly = true)
    public void checkColor(String color, Member member) {
        if (colorTypeRepository.findColorTypeByColorAndMember(color, member).isPresent()) {
            throw new ColorTypeDuplicatedException();
        }
    }

    @Transactional(readOnly = true)
    public List<ColorTypeResponse> getColorTypeResponses(Member member) {
        List<ColorType> colorTypes = colorTypeRepository.findColorTypesByMember(member);
        return createColorTypeResponsesWithColorTypes(colorTypes);
    }

    private List<ColorTypeResponse> createColorTypeResponsesWithColorTypes(List<ColorType> colorTypes) {
        return colorTypes.stream()
                .map(ColorTypeResponse::of)
                .toList();
    }

    @Transactional
    public ColorTypeResponse updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest, Member member) {
        checkColor(colorTypeUpdateRequest.newColor(), member);
        ColorType colorType = colorTypeRepository.findColorTypeByColorAndMember(colorTypeUpdateRequest.color(), member)
                .orElseThrow(ColorTypeNotFoundException::new);
        colorType.updateColorType(colorTypeUpdateRequest);
        return ColorTypeResponse.of(colorType);
    }

    @Transactional
    public void removeColor(String color, Member member) {
        ColorType colorType = colorTypeRepository.findColorTypeByColorAndMember(color, member).orElseThrow(ColorTypeNotFoundException::new);
        colorTypeRepository.delete(colorType);
    }
}
