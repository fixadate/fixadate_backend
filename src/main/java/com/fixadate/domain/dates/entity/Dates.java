package com.fixadate.domain.dates.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.common.entity.Calendar;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
	@Index(name = "calendar_id", columnList = "calendarId"),
	@Index(name = "date_range", columnList = "member_id,startsWhen,endsWhen")
})
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"member", "tag"})
public class Dates extends Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String etag;

	// TODO: [추후] 불필요, 삭제 필요
	private boolean reminders;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_adate_member_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_adate_tag_id"))
	private Tag tag;

	@Builder
	private Dates(
		final String title,
		final String notes,
		final String location,
		final LocalDateTime alertWhen,
		final LocalDateTime repeatFreq,
		final boolean ifAllDay,
		final LocalDateTime startsWhen,
		final LocalDateTime endsWhen,
		final String etag,
		final boolean reminders,
		final Member member,
		final Tag tag
	) {
		super.title = title;
		this.notes = notes;
		this.location = location;
		this.alertWhen = alertWhen;
		this.repeatFreq = repeatFreq;
		this.ifAllDay = ifAllDay;
		this.startsWhen = startsWhen;
		this.endsWhen = endsWhen;
		this.etag = etag;
		this.reminders = reminders;
		this.member = member;
		this.tag = tag;
	}

	public boolean isOwner(final Member member) {
		return this.member.equals(member);
	}

	public void removeTag() {
		this.tag = null;
	}

	// TODO: [추후] 구글 캘린더에서 수정이 될 때 ifAllDay, alertWhen, repeatFreq은 수정이 안 되나요? -> 답변 필요
	public void updateFrom(final Dates adate) {
		this.title = adate.title;
		this.notes = adate.notes;
		this.location = adate.location;
		this.ifAllDay = adate.ifAllDay;
		this.startsWhen = adate.startsWhen;
		this.endsWhen = adate.endsWhen;
		this.etag = adate.etag;
		this.reminders = adate.reminders;
	}


	public void updateTag(final Tag tag) {
		this.tag = tag;
	}
	public void updateReminders(final boolean reminders) {
		this.reminders = reminders;
	}

	@JsonIgnore
	public String getColor() {
		if (tag == null) {
			return null;
		}

		return tag.getColor();
	}
}
