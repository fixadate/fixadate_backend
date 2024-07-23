package com.fixadate.domain.tag.service;

import static com.fixadate.domain.tag.mapper.TagMapper.*;
import static com.fixadate.global.exception.ExceptionCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.event.object.AdateTagUpdateEvent;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.dto.response.TagResponse;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.mapper.TagMapper;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.exception.badrequest.TagBadRequestException;
import com.fixadate.global.exception.notfound.TagNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void registerTag(Member member, TagRequest tagRequest) {
		checkColor(tagRequest.name(), member);
		Tag tag = toEntity(member, tagRequest);
		tagRepository.save(tag);
	}

	@Transactional(readOnly = true)
	public void checkColor(String name, Member member) {
		if (tagRepository.findTagByNameAndMember(name, member).isPresent()) {
			throw new TagBadRequestException(ALREADY_EXISTS_TAG);
		}
	}

	@Transactional(readOnly = true)
	public List<TagResponse> getTagResponses(Member member) {
		List<Tag> tags = tagRepository.findTagsByMember(member);
		return createTagResponsesWithTags(tags);
	}

	private List<TagResponse> createTagResponsesWithTags(List<Tag> tags) {
		return tags.stream()
			.map(TagMapper::toResponse)
			.toList();
	}

	@Transactional
	public TagResponse updateTag(TagUpdateRequest tagUpdateRequest, Member member) {
		if (isValidString(tagUpdateRequest.newName())) {
			checkColor(tagUpdateRequest.newName(), member);
		}

		Tag tag = findTagByMemberAndColor(tagUpdateRequest.name(), member);

		if (isValidString(tagUpdateRequest.newName())) {
			isDefaultTag(tag);
		}

		tag.updateTag(tagUpdateRequest);

		if (isValidString(tagUpdateRequest.newColor())) {
			applicationEventPublisher.publishEvent(new AdateTagUpdateEvent(new ArrayList<>(tag.getAdates()), true));
		}

		return toResponse(tag);
	}

	private boolean isValidString(String str) {
		return str != null && !str.isEmpty();
	}

	@Transactional
	public Tag findTagByMemberAndColor(String name, Member member) {
		return tagRepository.findTagByNameAndMember(name, member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
	}

	public void isDefaultTag(Tag tag) {
		if (tag.isDefault()) {
			throw new TagBadRequestException(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG);
		}
	}

	@Transactional
	public void removeColor(String name, Member member) {
		Tag tag = findTagByMemberAndColor(name, member);

		isDefaultTag(tag);
		tag.deleteTag();

		tagRepository.delete(tag);
		applicationEventPublisher.publishEvent(new AdateTagUpdateEvent(new ArrayList<>(tag.getAdates()), false));
	}
}
