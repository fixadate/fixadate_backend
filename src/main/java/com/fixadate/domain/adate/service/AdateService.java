package com.fixadate.domain.adate.service;

import com.fixadate.domain.adate.repository.AdateRepository;
import com.google.api.services.calendar.Calendar.Events.Get;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdateService {
    private final AdateRepository adateRepository;

}
