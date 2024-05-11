package com.fixadate.domain.adate.entity;

import static com.fixadate.global.util.DateTimeParseUtil.*;

import java.time.LocalDateTime;

import com.fixadate.domain.adate.dto.request.AdateUpdateRequest;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.google.api.services.calendar.model.Event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(indexes = @Index(name = "calendar_id", columnList = "calendarId", unique = true))
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
	private String adateName;
	private Boolean ifAllDay;
	private LocalDateTime startsWhen;
	private LocalDateTime endsWhen;
	@Column(unique = true)
	private String calendarId;
	private String etag;
	private boolean reminders;
	private LocalDateTime version;
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "colorType_id")
	private ColorType colorType;

	public void updateFrom(Event event) {
		this.title = event.getSummary();
		this.notes = event.getDescription();
		this.location = event.getLocation();
		this.color = event.getColorId();
		this.startsWhen = checkStartDateTimeIsNull(event);
		this.endsWhen = checkEndDateTimeIsNull(event);
		this.calendarId = event.getId();
		this.etag = event.getEtag();
		this.reminders = event.getReminders().getUseDefault();
		this.status = event.getStatus();
	}

	public void updateAdate(AdateUpdateRequest adateUpdateRequest) {
		if (adateUpdateRequest.title() != null) {
			this.title = adateUpdateRequest.title();
		}
		if (adateUpdateRequest.notes() != null) {
			this.notes = adateUpdateRequest.notes();
		}
		if (adateUpdateRequest.location() != null) {
			this.location = adateUpdateRequest.location();
		}
		if (adateUpdateRequest.alertWhen() != null) {
			this.alertWhen = adateUpdateRequest.alertWhen();
		}
		if (adateUpdateRequest.repeatFreq() != null) {
			this.repeatFreq = adateUpdateRequest.repeatFreq();
		}
		if (adateUpdateRequest.color() != null) {
			this.color = adateUpdateRequest.color();
		}
		if (adateUpdateRequest.adateName() != null) {
			this.adateName = adateUpdateRequest.adateName();
		}
		this.ifAllDay = adateUpdateRequest.ifAllDay();

		if (adateUpdateRequest.startsWhen() != null) {
			this.startsWhen = adateUpdateRequest.startsWhen();
		}
		if (adateUpdateRequest.endsWhen() != null) {
			this.endsWhen = adateUpdateRequest.endsWhen();
		}
		this.reminders = adateUpdateRequest.reminders();
	}

	public static Adate getAdateFromEvent(Event event, Member member) {
		return Adate.builder()
			.title(event.getSummary())
			.notes(event.getDescription())
			.location(event.getLocation())
			.color(event.getColorId())
			.startsWhen(checkStartDateTimeIsNull(event))
			.endsWhen(checkEndDateTimeIsNull(event))
			.ifAllDay(checkEventIsAllDayType(event))
			.calendarId(event.getId())
			.etag(event.getEtag())
			.reminders(event.getReminders().getUseDefault())
			.status(event.getStatus())
			.member(member)
			.build();
	}

	private static LocalDateTime checkStartDateTimeIsNull(Event event) {
		if (event.getStart().getDateTime() == null) {
			return getLocalDateTimeFromDate(event.getStart().getDate());
		}
		return getLocalDateTimeFromDateTime(event.getStart().getDateTime());
	}

	private static LocalDateTime checkEndDateTimeIsNull(Event event) {
		if (event.getStart().getDateTime() == null) {
			return getLocalDateTimeFromDate(event.getEnd().getDate());
		}
		return getLocalDateTimeFromDateTime(event.getEnd().getDateTime());
	}

	private static boolean checkEventIsAllDayType(Event event) {
		return event.getStart().getDateTime() == null;
	}

	public void setColorType(ColorType colorType) {
		this.colorType = colorType;
	}
}
