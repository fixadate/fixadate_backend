package com.fixadate.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final RedisTemplate<String, Object> redisTemplate;
    @GetMapping("/redistest")
    public String redisTest() {
        redisTemplate.opsForValue().set("yongjun", "yongjun");
        return (String) redisTemplate.opsForValue().get("yongjun");
    }
}
