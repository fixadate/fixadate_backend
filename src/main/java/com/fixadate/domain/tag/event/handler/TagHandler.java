package com.fixadate.domain.tag.event.handler;

import static com.fixadate.global.exception.ExceptionCode.*;
import static com.fixadate.global.util.constant.ConstantValue.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.event.object.TagMemberSettingEvent;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.exception.notfound.TagNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagHandler {
	private final TagRepository tagRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@EventListener
	public void setTagEvent(TagSettingEvent event) {
		Adate adate = event.adate();
		Member member = adate.getMember();
		String tagName = event.tagName();

		Tag tag = tagRepository.findTagByNameAndMember(tagName, member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
		adate.updateTag(tag);
	}

	@EventListener
	public void setTagMemberEvent(TagMemberSettingEvent event) {
		Member member = event.member();

		Tag tag = Tag.builder()
			.color(GOOGLE_CALENDAR_COLOR.getValue())
			.name(GOOGLE_CALENDAR.getValue())
			.isDefault(true)
			.member(member)
			.build();

		tagRepository.save(tag);
	}

}
