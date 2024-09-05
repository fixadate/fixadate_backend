package com.fixadate.domain.adate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fixadate.domain.auth.entity.BaseTimeEntity;
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
public class Adate extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 500)
	private String notes;

	@Column(length = 300)
	private String location;

	private LocalDateTime alertWhen;

	private LocalDateTime repeatFreq;

	private String color;

	// TODO: [추후] if 대신 is로 수정 & 불필요한 필드 삭제
	private boolean ifAllDay;

	private LocalDateTime startsWhen;

	private LocalDateTime endsWhen;

	@Column(unique = true)
	private String calendarId;

	private String etag;

	private boolean reminders;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_adate_member_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_adate_tag_id"))
	@JsonIgnore
	private Tag tag;

	@Builder
	private Adate(
		final String title,
		final String notes,
		final String location,
		final LocalDateTime alertWhen,
		final LocalDateTime repeatFreq,
		final String color,
		final boolean ifAllDay,
		final LocalDateTime startsWhen,
		final LocalDateTime endsWhen,
		final String calendarId,
		final String etag,
		final boolean reminders,
		final Member member,
		final Tag tag
	) {
		this.title = title;
		this.notes = notes;
		this.location = location;
		this.alertWhen = alertWhen;
		this.repeatFreq = repeatFreq;
		this.color = color;
		this.ifAllDay = ifAllDay;
		this.startsWhen = startsWhen;
		this.endsWhen = endsWhen;
		this.calendarId = calendarId;
		this.etag = etag;
		this.reminders = reminders;
		this.member = member;
		this.tag = tag;
	}

	public boolean isOwner(final Member member) {
		return this.member.equals(member);
	}

	public void removeTagAndColor() {
		this.color = null;
		this.tag = null;
	}

	// TODO: [추후] 구글 캘린더에서 수정이 될 때 ifAllDay, alertWhen, repeatFreq은 수정이 안 되나요? -> 답변 필요
	public void updateFrom(final Adate adate) {
		this.title = adate.title;
		this.notes = adate.notes;
		this.location = adate.location;
		this.color = adate.color;
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

		if (tag != null) {
			this.color = tag.getColor();
		}
	}

	// TODO: [의견] updateColor로는 adate에 설정되어 있는 tag의 color로 수정된다는 것을 이해하기 어렵다고 생각해 아래와 같이 변경해봤습니다.
	//  혹은 deleteTag에서 adate의 removeTagAndColor를 호출하는 것처럼, tag의 색상 update 시 updateTage(tag.color)를 통해 호출하는 건 어떤가 싶습니다.
	public void
	refreshColorFromCurrentTag() {
		if (this.tag != null) {
			this.color = this.tag.getColor();
		}
	}
}
