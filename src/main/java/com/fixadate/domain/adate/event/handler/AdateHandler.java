package com.fixadate.domain.adate.event.handler;

import static com.fixadate.domain.adate.mapper.AdateMapper.toEntity;
import static com.fixadate.global.util.constant.ConstantValue.CALENDAR_CANCELLED;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.adate.event.object.ExternalCalendarSettingEvent;
import com.fixadate.domain.adate.event.object.AdateTagUpdateEvent;
import com.fixadate.domain.adate.mapper.AdateMapper;
import com.fixadate.domain.adate.service.AdateService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.util.constant.ExternalCalendar;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdateHandler {

	private final AdateService adateService;

	// TODO: [추후] 구글 외 외부 캘린더도 적용 가능하도록 확장 필요
	@EventListener
	public void setExternalCalendarToAdate(final ExternalCalendarSettingEvent event) {
		final Event googleEvent = event.event();
		final Member member = event.member();
		final ExternalCalendar calendar = event.externalCalendar();

		final Optional<Adate> adateOptional = adateService.getAdateByCalendarId(googleEvent.getId());

		if (adateOptional.isEmpty()) {
			final Adate adate = toEntity(googleEvent, member);
			adateService.registerExternalCalendarToAdate(adate, calendar);
			return;
		}

		if (googleEvent.getStatus().equals(CALENDAR_CANCELLED.getValue())) {
			adateOptional.ifPresent(adateService::removeAdate);
			return;
		}

		adateOptional.ifPresent(originAdate -> {
			if (!originAdate.getEtag().equals(googleEvent.getEtag())) {
				final Adate adate = AdateMapper.toEntity(googleEvent);
				originAdate.updateFrom(adate);
			}
		});
	}

	@Async
	@EventListener
	public void updateAdateTagEvent(final AdateTagUpdateEvent adateTagUpdateEvent) {
		adateTagUpdateEvent.adates()
						   .forEach(Adate::refreshColorFromCurrentTag);
	}
}
