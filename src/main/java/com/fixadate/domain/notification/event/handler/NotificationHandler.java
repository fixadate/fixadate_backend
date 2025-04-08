package com.fixadate.domain.notification.event.handler;

import com.fixadate.domain.notification.event.object.AliveAlarmEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationCancelEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationChoiceEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationConfirmEvent;
import com.fixadate.domain.notification.event.object.DatesCoordinationCreateEvent;
import com.fixadate.domain.notification.service.NotificationService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHandler {

	private final NotificationService notificationService;

	@EventListener
	public void setAliveAlarmCheck(final AliveAlarmEvent event) {
		notificationService.sendEvent(event.memberId());
	}

	@EventListener
	public void sendDatesCoordinationCreateEvent(final DatesCoordinationCreateEvent event)
		throws IOException {
		notificationService.sendDatesCoordinationCreateEvent(event.teamMemberList(), event.datesCoordinationDto());
	}

	@EventListener
	public void sendDatesCoordinationChoiceEvent(final DatesCoordinationChoiceEvent event)
		throws IOException {
		notificationService.sendDatesCoordinationChoiceEvent(event.proponent(), event.datesCoordinationDto());
	}

	@EventListener
	public void sendDatesCoordinationConfirmEvent(final DatesCoordinationConfirmEvent event)
		throws IOException {
		notificationService.sendDatesCoordinationConfirmEvent(event.participant(), event.datesDto());
	}

	@EventListener
	public void sendDatesCoordinationCancelEvent(final DatesCoordinationCancelEvent event)
		throws IOException {
		notificationService.sendDatesCoordinationCancelEvent(event.participant(), event.datesCoordinationDto());
	}
}
