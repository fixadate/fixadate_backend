package com.fixadate.domain.colortype.service;

import static com.fixadate.global.exception.ExceptionCode.*;

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
		checkColor(colorTypeRequest.name(), member);
		ColorType colorType = colorTypeRequest.toEntity(member);
		colorTypeRepository.save(colorType);
	}

	@Transactional(readOnly = true)
	public void checkColor(String name, Member member) {
		if (colorTypeRepository.findColorTypeByNameAndMember(name, member).isPresent()) {
			throw new ColorTypeBadRequestException(ALREADY_EXISTS_COLORTYPE);
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
		if (isValidString(colorTypeUpdateRequest.newName())) {
			checkColor(colorTypeUpdateRequest.newName(), member);
		}

		ColorType colorType = findColorTypeByMemberAndColor(colorTypeUpdateRequest.name(), member);

		if (isValidString(colorTypeUpdateRequest.newName())) {
			isDefaultColorType(colorType);
		}

		colorType.updateColorType(colorTypeUpdateRequest);

		if (isValidString(colorTypeUpdateRequest.newColor())) {
			updateAdateColor(colorType.getAdates());
		}

		return ColorTypeResponse.of(colorType);
	}

	private boolean isValidString(String str) {
		return str != null && !str.isEmpty();
	}

	@Transactional
	public ColorType findColorTypeByMemberAndColor(String name, Member member) {
		return colorTypeRepository.findColorTypeByNameAndMember(name, member)
			.orElseThrow(() -> new ColorTypeNotFoundException(NOT_FOUND_COLORTYPE_MEMBER_COLOR));
	}

	@Transactional
	public void updateAdateColor(List<Adate> adates) {
		adates.forEach(Adate::updateColor);
		adateRepository.saveAll(adates);
	}

	@Transactional
	public void isDefaultColorType(ColorType colorType) {
		if (colorType.isDefault()) {
			throw new ColorTypeBadRequestException(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_COLORTYPE);
		}
	}

	@Transactional
	public void removeColor(String name, Member member) {
		ColorType colorType = findColorTypeByMemberAndColor(name, member);
		isDefaultColorType(colorType);
		removeAdateColorType(colorType.getAdates());
		colorTypeRepository.delete(colorType);
	}

	@Transactional
	public void removeAdateColorType(List<Adate> adates) {
		adates.forEach(Adate::removeColorTypeAndColor);
		adateRepository.saveAll(adates);
	}
}
