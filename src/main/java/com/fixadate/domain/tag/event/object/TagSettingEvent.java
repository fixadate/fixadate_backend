package com.fixadate.domain.tag.event.object;

import com.fixadate.domain.adate.entity.Adate;

public record TagSettingEvent(Adate adate, String tagName) {
}

// todo: 추후 변경
// public record TagSettingEvent<T extends Calendar>(T calendar, String tagName) {
//}