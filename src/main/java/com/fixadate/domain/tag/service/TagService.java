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
	public void registerTag(Member member, TagRequest tagRequest) {
		checkColor(tagRequest.name(), member);
		tagRepository.save(toEntity(member, tagRequest));
	}

	public void checkColor(String name, Member member) {
		if (tagRepository.findTagByNameAndMember(name, member).isPresent()) {
			throw new TagBadRequestException(ALREADY_EXISTS_TAG);
		}
	}

	@Transactional(readOnly = true)
	public List<TagResponse> getTagResponses(Member member) {
		List<Tag> tags = tagRepository.findTagsByMember(member);

		return tags.stream()
				   .map(TagMapper::toResponse)
				   .toList();
	}

	@Transactional
	public TagResponse updateTag(TagUpdateRequest tagUpdateRequest, Member member) {
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
	public Tag findTagByMemberAndColor(String name, Member member) {
		return tagRepository.findTagByNameAndMember(name, member)
							.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
	}

	public void isSystemDefinedTag(Tag tag) {
		if (tag.isSystemDefined()) {
			throw new TagBadRequestException(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG);
		}
	}

	@Transactional
	public void removeColor(String name, Member member) {
		Tag tag = findTagByMemberAndColor(name, member);

		isSystemDefinedTag(tag);
		tag.deleteTag();

		tagRepository.delete(tag);
	}
}
