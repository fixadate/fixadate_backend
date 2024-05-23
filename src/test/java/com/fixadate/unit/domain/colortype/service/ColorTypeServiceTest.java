package com.fixadate.unit.domain.colortype.service;

import static com.fixadate.unit.domain.colortype.fixture.ColorTypeFixture.*;
import static com.fixadate.unit.domain.member.fixture.MemberFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.colortype.dto.request.ColorTypeRequest;
import com.fixadate.domain.colortype.dto.response.ColorTypeResponse;
import com.fixadate.domain.colortype.repository.ColorTypeRepository;
import com.fixadate.domain.colortype.service.ColorTypeService;
import com.fixadate.domain.member.entity.Member;

@ExtendWith(MockitoExtension.class)
@Transactional
public class ColorTypeServiceTest {

	@InjectMocks
	private ColorTypeService colorTypeService;
	@Mock
	private ColorTypeRepository colorTypeRepository;
	@Mock
	private AdateRepository adateRepository;

	@DisplayName("colorType 저장하기")
	@Test
	void registColorTypeTest() {
		ColorTypeRequest colorTypeRequest = new ColorTypeRequest(
			COLOR_TYPE.getColor(),
			COLOR_TYPE.getName()
		);
		assertDoesNotThrow(() -> colorTypeService.registColorType(MEMBER, colorTypeRequest));
	}

	@DisplayName("colorType 존재하는지 확인하기")
	@Test
	void checkColorTest() {
		assertDoesNotThrow(() -> colorTypeService.checkColor("red", MEMBER));
	}

	@DisplayName("member의 colorType 조회하기")
	@Test
	void getColorTypeResponsesTest() {
		given(colorTypeRepository.findColorTypesByMember(any(Member.class))).willReturn(COLOR_TYPES);

		List<ColorTypeResponse> responses = colorTypeService.getColorTypeResponses(MEMBER);
		assertEquals(COLOR_TYPES.size(), responses.size());
		assertEquals(COLOR_TYPES.get(0).getColor(), responses.get(0).color());
	}

	@DisplayName("color 삭제하기")
	@Test
	void removeColorTest() {
		given(colorTypeRepository.findColorTypeByNameAndMember(COLOR_TYPE.getColor(), MEMBER)).willReturn(
			Optional.of(COLOR_TYPE));
		assertDoesNotThrow(() -> colorTypeService.removeColor(COLOR_TYPE.getColor(), MEMBER));
	}
}
