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

@Component
@RequiredArgsConstructor
public class AdateHandler {

	private final AdateService adateService;

	@EventListener
	public void setAdateEvent(final AdateCalendarSettingEvent event) {
		final Event googleEvent = event.event();
		final Member member = event.member();

		final Optional<Adate> adateOptional = adateService.getAdateByCalendarId(googleEvent.getId());

		if (googleEvent.getStatus().equals(CALENDAR_CANCELLED.getValue())) {
			adateOptional.ifPresent(adateService::removeAdate);
			return;
		}

		adateOptional.ifPresent(originAdate -> {
			if (!originAdate.getEtag().equals(googleEvent.getEtag())) {
				final Adate adate = eventToEntity(googleEvent);
				originAdate.updateFrom(adate);
			}
		});

		if (adateOptional.isEmpty()) {
			final Adate adate = eventToEntity(googleEvent, member);
			adateService.registerEvent(adate);
		}
	}

	@Async
	@EventListener
	@Transactional(propagation = Propagation.SUPPORTS)
	public void updateAdateTagEvent(final AdateTagUpdateEvent adateTagUpdateEvent) {
		adateTagUpdateEvent.adates()
						   .forEach(Adate::refreshColorFromCurrentTag);
	}
}
