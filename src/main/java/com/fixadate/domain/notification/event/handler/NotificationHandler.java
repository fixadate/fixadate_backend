package com.fixadate.domain.notification.event.handler;

import com.fixadate.domain.notification.event.object.AliveAlarmEvent;
import com.fixadate.domain.notification.service.NotificationService;
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
}
