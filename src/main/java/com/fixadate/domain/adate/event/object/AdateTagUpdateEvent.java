package com.fixadate.domain.adate.event.object;

import java.util.List;

import com.fixadate.domain.adate.entity.Adate;

public record AdateTagUpdateEvent(List<Adate> adates) {
}
