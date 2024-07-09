package com.fixadate.unit.domain.tag.service;

import static com.fixadate.unit.domain.member.fixture.MemberFixture.*;
import static com.fixadate.unit.domain.tag.fixture.TagFixture.*;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.domain.tag.service.TagService;

@ExtendWith(MockitoExtension.class)
@Transactional
public class TagServiceTest {

	@InjectMocks
	private TagService tagService;
	@Mock
	private TagRepository tagRepository;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@DisplayName("tag 저장하기")
	@Test
	void registerTageTest() {
		TagRequest tagRequest = new TagRequest(
			TAG.getColor(),
			TAG.getName()
		);
		assertDoesNotThrow(() -> tagService.registerTag(MEMBER, tagRequest));
	}

	@DisplayName("tag 존재하는지 확인하기")
	@Test
	void checkColorTest() {
		assertDoesNotThrow(() -> tagService.checkColor("red", MEMBER));
	}

	@DisplayName("member의 tag 조회하기")
	@Test
	void getTagResponsesTest() {
		given(tagRepository.findTagsByMember(any(Member.class))).willReturn(TAGS);

		List<TagResponse> responses = tagService.getTagResponses(MEMBER);
		assertEquals(TAGS.size(), responses.size());
		assertEquals(TAGS.get(0).getColor(), responses.get(0).color());
	}

	@DisplayName("color 삭제하기")
	@Test
	void removeColorTest() {
		given(tagRepository.findTagByNameAndMember(TAG.getColor(), MEMBER)).willReturn(
			Optional.of(TAG));
		assertDoesNotThrow(() -> tagService.removeColor(TAG.getColor(), MEMBER));
	}
}
