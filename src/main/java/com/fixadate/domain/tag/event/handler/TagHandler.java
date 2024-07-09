package com.fixadate.domain.tag.event.handler;

import static com.fixadate.global.exception.ExceptionCode.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.event.object.TagSettingEvent;
import com.fixadate.domain.tag.repository.TagRepository;
import com.fixadate.global.exception.notFound.TagNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 8.
 */
@Component
@RequiredArgsConstructor
public class TagHandler {
	private final TagRepository tagRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@EventListener
	public void setTagEvent(TagSettingEvent event) {
		Adate adate = event.adate();
		Member member = event.member();
		String tagName = event.tagName();

		Tag tag = tagRepository.findTagByNameAndMember(tagName, member)
			.orElseThrow(() -> new TagNotFoundException(NOT_FOUND_TAG_MEMBER_NAME));
		adate.setTag(tag);
	}
}
