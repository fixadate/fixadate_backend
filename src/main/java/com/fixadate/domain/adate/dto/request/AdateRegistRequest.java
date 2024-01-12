package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdateRegistRequest {
    @Column(nullable = false)
    private String title;
    private String notes;
    private String location;
    private Date alertWhen;
    private Date repeatFreq;
    private String color;
    private String adateName;

    private Boolean ifAllDay;
    private LocalDateTime startsWhen;
    private LocalDateTime endsWhen;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean reminders;

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
                .startDate(startDate)
                .endDate(endDate)
                .calendarId(generateRandomString(20))
                .reminders(reminders)
                .build();
    }
    public String generateRandomString(int length) {
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
