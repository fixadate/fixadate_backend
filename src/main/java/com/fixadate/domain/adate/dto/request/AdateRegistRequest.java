package com.fixadate.domain.adate.dto.request;

import static com.fixadate.global.util.RandomValueUtil.*;

import java.time.LocalDateTime;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;

public record AdateRegistRequest(
	@NotBlank(message = "Adate title cannot be blank") String title,
	String notes,
	String location,
	LocalDateTime alertWhen,
	LocalDateTime repeatFreq,
	String color,
	String adateName,
	boolean ifAllDay,
	LocalDateTime startsWhen,
	LocalDateTime endsWhen,
	boolean reminders
) {
	public Adate toEntity(Member member) {
		return Adate.builder()
			.title(title)
			.notes(notes)
			.location(location)
			.alertWhen(alertWhen)
			.repeatFreq(repeatFreq)
			.color(color)
			.adateName(adateName)
			.ifAllDay(ifAllDay)
			.startsWhen(startsWhen)
			.endsWhen(endsWhen)
			.calendarId(createRandomString(10))
			.reminders(reminders)
			.member(member)
			.build();
	}

}
