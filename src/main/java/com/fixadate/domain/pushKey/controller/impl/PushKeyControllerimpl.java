package com.fixadate.domain.pushKey.controller.impl;

import com.fixadate.domain.pushKey.controller.PushKeyController;
import com.fixadate.domain.pushKey.service.PushKeyService;
import com.fixadate.global.annotation.RestControllerWithMapping;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestControllerWithMapping("/key")
@RequiredArgsConstructor
public class PushKeyControllerimpl implements PushKeyController{

    private final PushKeyService pushKeyService;

    @PostMapping()
    public ResponseEntity<Void> registPushKey(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                              @RequestParam String pushKey) {
        String memberId = memberPrincipal.getMemberId();
        pushKeyService.registPushKey(pushKey, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
