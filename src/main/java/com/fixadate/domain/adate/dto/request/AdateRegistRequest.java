package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

public record AdateRegistRequest(
        @Column(nullable = false) String title,
        String notes,
        String location,
        Date alertWhen,
        Date repeatFreq,
        String color,
        String adateName,
        Boolean ifAllDay,
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
                .calendarId(generateRandomString(20))
                .reminders(reminders)
                .member(member)
                .build();
    }

    private String generateRandomString(int length) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(62);
            char randomChar = (char) (randomIndex < 10
                    ? '0' + randomIndex
                    : (randomIndex < 36 ? 'A' + randomIndex - 10 : 'a' + randomIndex - 36));
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
