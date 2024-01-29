package com.fixadate.domain.invitation.service;

import com.fixadate.domain.invitation.dto.request.InvitationRequest;
import com.fixadate.domain.invitation.dto.response.InvitationResponse;
import com.fixadate.domain.invitation.entity.Invitation;
import com.fixadate.domain.invitation.exception.InvitationNotFountException;
import com.fixadate.domain.invitation.repository.InvitationRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.MemberService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void registInvitation(Member inviter, InvitationRequest invitationRequest) {
        invitationRepository.save(createInviation(inviter, invitationRequest));
    }

    private Invitation createInviation(Member inviter, InvitationRequest invitationRequest) {
        return Invitation.builder()
                .inviter(inviter.getEmail())
                .invitee(invitationRequest.getInviteeEmail())
                .team(invitationRequest.getTeamId())
                .expiration(3_600_000L)
                .build();
    }

    public InvitationResponse getInvitationFromId(String id) {
        Invitation invitations = invitationRepository.findById(id).orElseThrow(InvitationNotFountException::new);
        List<Object> list = redisTemplate.opsForList().range("invitee:" + "kevin09288@daum.net", 0 ,-1);
        log.info(list.size() + "몇 개?");
        return InvitationResponse.of(invitations);
    }
}
