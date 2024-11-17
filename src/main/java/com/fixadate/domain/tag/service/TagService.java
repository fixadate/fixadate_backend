package com.fixadate.domain.tag.service;

import static com.fixadate.domain.tag.mapper.TagMapper.toEntity;
import static com.fixadate.domain.tag.mapper.TagMapper.toResponse;
import static com.fixadate.global.exception.ExceptionCode.ALREADY_EXISTS_TAG;
import static com.fixadate.global.exception.ExceptionCode.CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG;
import static com.fixadate.global.exception.ExceptionCode.NOT_FOUND_TAG_MEMBER_NAME;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.TagRegisterDto;
import com.fixadate.domain.tag.dto.TagUpdateDto;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.mapper.TagMapper;
import com.fixadate.domain.tag.service.repository.TagRepository;
import com.fixadate.global.exception.badrequest.TagBadRequestException;
import com.fixadate.global.exception.notfound.TagNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;

	@Transactional
	public void registerTag(final TagRegisterDto tagRegisterDto) {
		final TagRequest tagRequest = tagRegisterDto.tagRequest();
		final Member member = tagRegisterDto.member();

		checkColor(tagRequest.name(), member);
		tagRepository.save(toEntity(member, tagRequest));
	}

	public void checkColor(final String name, final Member member) {
		if (tagRepository.findTagByNameAndMember(name, member).isPresent()) {
			throw new TagBadRequestException(ALREADY_EXISTS_TAG);
		}
	}

	@Transactional(readOnly = true)
	public List<TagResponse> getTagResponses(final Member member) {
		List<Tag> tags = tagRepository.findTagsByMember(member);

		return tags.stream()
				   .map(TagMapper::toResponse)
				   .toList();
	}

	@Transactional
	public TagResponse updateTag(final TagUpdateDto tagUpdateDto) {
		final TagUpdateRequest tagUpdateRequest = tagUpdateDto.tagUpdateRequest();
		final Member member = tagUpdateDto.member();

		Tag tag = findTagByMemberAndColor(tagUpdateRequest.name(), member);

		if (StringUtils.hasText(tagUpdateRequest.newName())) {
			checkColor(tagUpdateRequest.newName(), member);
			isSystemDefinedTag(tag);
			tag.updateTagName(tagUpdateRequest.newName());
		}

		if (StringUtils.hasText(tagUpdateRequest.newColor())) {
			tag.updateTagColor(tagUpdateRequest.newColor());
		}

		return toResponse(tag);
	}


	@Transactional
	public Tag findTagByMemberAndColor(final String name, final Member member) {
		return tagRepository.findTagByNameAndMember(name, member)
							.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
	}

	public void isSystemDefinedTag(final Tag tag) {
		if (tag.isSystemDefined()) {
			throw new TagBadRequestException(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG);
		}
	}

	@Transactional
	public void removeColor(final String name, final Member member) {
		Tag tag = findTagByMemberAndColor(name, member);

		isSystemDefinedTag(tag);

		if (tag.getAdates() != null && !tag.getAdates().isEmpty()) {
			tag.deleteTag();
		}
		tagRepository.delete(tag);
	}
}
