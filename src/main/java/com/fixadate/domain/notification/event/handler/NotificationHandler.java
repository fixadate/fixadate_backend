package com.fixadate.domain.notification.event.handler;

import com.fixadate.domain.notification.event.object.AliveAlarmEvent;
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
}
