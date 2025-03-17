package com.fixadate.domain.adate.entity;

import com.fixadate.domain.common.entity.Calendar;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
	@Index(name = "calendar_id", columnList = "calendarId"),
	@Index(name = "date_range", columnList = "member_id,startsWhen,endsWhen")
})
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"member", "tag"})
public class Adate extends Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 300)
	private String location;

	private LocalDateTime alertWhen;

	private LocalDateTime repeatFreq;

	// TODO: [추후] if 대신 is로 수정 & 불필요한 필드 삭제
	private boolean ifAllDay;

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
	private Adate(
		final String title,
		final String notes,
		final String location,
		final LocalDateTime alertWhen,
		final LocalDateTime repeatFreq,
		final boolean ifAllDay,
		final LocalDateTime startsWhen,
		final LocalDateTime endsWhen,
		final String calendarId,
		final String etag,
		final boolean reminders,
		final Member member,
		final Tag tag
	) {
		super.title = title;
		super.notes = notes;
		this.location = location;
		this.alertWhen = alertWhen;
		this.repeatFreq = repeatFreq;
		this.ifAllDay = ifAllDay;
		super.startsWhen = startsWhen;
		super.endsWhen = endsWhen;
		super.calendarId = calendarId;
		super.etag = etag;
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
	public void updateFrom(final Adate adate) {
		this.title = adate.title;
		this.notes = adate.notes;
		this.location = adate.location;
		this.ifAllDay = adate.ifAllDay;
		this.startsWhen = adate.startsWhen;
		this.endsWhen = adate.endsWhen;
		this.calendarId = adate.calendarId;
		this.etag = adate.etag;
		this.reminders = adate.reminders;
	}

	public void updateTitle(final String title) {
		this.title = title;
	}

	public void updateNotes(final String notes) {
		this.notes = notes;
	}

	public void updateLocation(final String location) {
		this.location = location;
	}

	public void updateAlertWhen(final LocalDateTime alertWhen) {
		this.alertWhen = alertWhen;
	}

	public void updateRepeatFreq(final LocalDateTime repeatFreq) {
		this.repeatFreq = repeatFreq;
	}

	public void updateIfAllDay(final boolean ifAllDay) {
		this.ifAllDay = ifAllDay;
	}

	public void updateStartsWhen(final LocalDateTime startsWhen) {
		this.startsWhen = startsWhen;
	}

	public void updateEndsWhen(final LocalDateTime endsWhen) {
		this.endsWhen = endsWhen;
	}

	public void updateReminders(final boolean reminders) {
		this.reminders = reminders;
	}

	public void updateTag(final Tag tag) {
		this.tag = tag;
	}

	@JsonIgnore
	public String getColor() {
		if (tag == null) {
			return null;
		}

		return tag.getColor();
	}

	@JsonIgnore
	public String getTagName() {
		if (tag == null) {
			return null;
		}

		return tag.getName();
	}
}
