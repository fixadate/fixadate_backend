package com.fixadate.domain.adate.event.handler;

import static com.fixadate.domain.adate.mapper.AdateMapper.eventToEntity;
import static com.fixadate.global.util.constant.ConstantValue.CALENDAR_CANCELLED;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.event.object.AdateCalendarSettingEvent;
import com.fixadate.domain.adate.event.object.AdateTagUpdateEvent;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;

/**
 * @author yongjunhong
 * @since 2024. 7. 9.
 */
@Component
@RequiredArgsConstructor
public class AdateHandler {
	private final AdateService adateService;

	@EventListener
	public void setAdateEvent(AdateCalendarSettingEvent event) {
		Event googleEvent = event.event();
		Member member = event.member();

		Optional<Adate> adateOptional = adateService.getAdateByCalendarId(googleEvent.getId());

		if (googleEvent.getStatus().equals(CALENDAR_CANCELLED.getValue())) {
			adateOptional.ifPresent(adateService::removeAdate);
			return;
		}

		adateOptional.ifPresent(adate -> {
			if (!adate.getEtag().equals(googleEvent.getEtag())) {
				adate.updateFrom(eventToEntity(googleEvent));
			}
		});

		if (adateOptional.isEmpty()) {
			adateService.registerEvent(googleEvent, member);
		}
	}

	@Async
	@EventListener
	@Transactional(propagation = Propagation.SUPPORTS)
	public void updateAdateTagEvent(AdateTagUpdateEvent adateTagUpdateEvent) {
		adateTagUpdateEvent.adates().forEach(Adate::updateColor);
	}
}
