package com.fixadate.domain.tag.event.object;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;

/**
 *
 * @author yongjunhong
 * @since 2024. 7. 9.
 */
public record TagSettingEvent(Adate adate, Member member, String tagName) {
}
