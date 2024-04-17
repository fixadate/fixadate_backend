package com.fixadate.domain.team.controller;

import com.fixadate.global.annotation.RestControllerWithMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestControllerWithMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final RedisTemplate<String, Object> redisTemplate;
    @GetMapping("/redistest")
    public String redisTest() {
        redisTemplate.opsForValue().set("yongjun", "yongjun");
        return (String) redisTemplate.opsForValue().get("yongjun");
    }
    @GetMapping("/health-check")
    public ResponseEntity<Void> checkHealthStatus() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
