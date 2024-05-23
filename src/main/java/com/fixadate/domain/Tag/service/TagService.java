package com.fixadate.domain.Tag.service;

import static com.fixadate.global.exception.ExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.Tag.dto.request.TagRequest;
import com.fixadate.domain.Tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.Tag.dto.response.TagResponse;
import com.fixadate.domain.Tag.entity.Tag;
import com.fixadate.domain.Tag.repository.TagRepository;
import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.repository.AdateRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.exception.badRequest.TagBadRequestException;
import com.fixadate.global.exception.notFound.TagNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;
	private final AdateRepository adateRepository;

	@Transactional
	public void registTag(Member member, TagRequest tagRequest) {
		checkColor(tagRequest.name(), member);
		Tag tag = tagRequest.toEntity(member);
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
			.map(TagResponse::of)
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
			updateAdateColor(tag.getAdates());
		}

		return TagResponse.of(tag);
	}

	private boolean isValidString(String str) {
		return str != null && !str.isEmpty();
	}

	@Transactional
	public Tag findTagByMemberAndColor(String name, Member member) {
		return tagRepository.findTagByNameAndMember(name, member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
	}

	@Transactional
	public void updateAdateColor(List<Adate> adates) {
		adates.forEach(Adate::updateColor);
		adateRepository.saveAll(adates);
	}

	@Transactional
	public void isDefaultTag(Tag tag) {
		if (tag.isDefault()) {
			throw new TagBadRequestException(CAN_NOT_UPDATE_OR_REMOVE_DEFAULT_TAG);
		}
	}

	@Transactional
	public void removeColor(String name, Member member) {
		Tag tag = findTagByMemberAndColor(name, member);
		isDefaultTag(tag);
		removeAdateTag(tag.getAdates());
		tagRepository.delete(tag);
	}

	@Transactional
	public void removeAdateTag(List<Adate> adates) {
		adates.forEach(Adate::removeTagAndColor);
		adateRepository.saveAll(adates);
	}
}
