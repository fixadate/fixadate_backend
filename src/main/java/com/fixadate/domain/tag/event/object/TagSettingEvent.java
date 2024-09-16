package com.fixadate.domain.tag.event.object;

import com.fixadate.domain.adate.entity.Adate;

public record TagSettingEvent(Adate adate, String tagName) {
}
