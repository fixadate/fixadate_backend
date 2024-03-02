package com.fixadate.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    public String getRandomNickname(List<String> strings) {
        return strings.get(random.nextInt(strings.size())).trim();
    }
}
