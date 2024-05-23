package com.fixadate.domain.colortype.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.exception.badRequest.ColorTypeBadRequestException;
import com.fixadate.global.exception.notFound.ColorTypeNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ColorTypeService {

	private final ColorTypeRepository colorTypeRepository;
	private final AdateRepository adateRepository;

	@Transactional
	public void registColorType(Member member, ColorTypeRequest colorTypeRequest) {
		checkColor(colorTypeRequest.color(), member);
		ColorType colorType = colorTypeRequest.toEntity(member);
		colorTypeRepository.save(colorType);
	}

	@Transactional(readOnly = true)
	public void checkColor(String color, Member member) {
		if (colorTypeRepository.findColorTypeByColorAndMember(color, member).isPresent()) {
			throw new ColorTypeBadRequestException(ExceptionCode.ALREADY_EXISTS_COLORTYPE);
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
			.orElseThrow(() -> new ColorTypeNotFoundException(ExceptionCode.NOT_FOUND_COLORTYPE_MEMBER_COLOR));
		colorType.updateColorType(colorTypeUpdateRequest);

		updateAdateColor(colorType.getAdates());
		return ColorTypeResponse.of(colorType);
	}

	@Transactional
	public void updateAdateColor(List<Adate> adates) {
		adates.forEach(Adate::updateColor);
		adateRepository.saveAll(adates);
	}

	@Transactional
	public void removeColor(String color, Member member) {
		ColorType colorType = colorTypeRepository.findColorTypeByColorAndMember(color, member)
			.orElseThrow(() -> new ColorTypeNotFoundException(ExceptionCode.NOT_FOUND_COLORTYPE_MEMBER_COLOR));

		removeAdateColorType(colorType.getAdates());
		colorTypeRepository.delete(colorType);
	}

	@Transactional
	public void removeAdateColorType(List<Adate> adates) {
		adates.forEach(Adate::removeColorTypeAndColor);
		adateRepository.saveAll(adates);
	}
}
