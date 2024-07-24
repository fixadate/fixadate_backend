package com.fixadate.domain.adate.event.object;

import java.util.List;

import com.fixadate.domain.adate.entity.Adate;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 9.
 */
public record AdateTagUpdateEvent(List<Adate> adates) {
}
