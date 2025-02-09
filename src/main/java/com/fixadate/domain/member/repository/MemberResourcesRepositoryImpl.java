package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.MemberResources;
import com.fixadate.domain.member.entity.ResourceType;
import com.fixadate.domain.member.service.repository.MemberResourcesRepository;
import com.fixadate.global.exception.notfound.NotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberResourcesRepositoryImpl implements MemberResourcesRepository {
    private final MemberResourcesJpaRepository memberResourcesJpaRepository;

    @Override
    public MemberResources getMemberResources(Member member) {
        Optional<MemberResources> memberResources = memberResourcesJpaRepository.findByMember(member);
        return memberResources.orElseThrow(
            () -> new NotFoundException(null)
        );
    }

    @Override
    public boolean addMemberResources(Member member, ResourceType resourceType, int cnt) {
        MemberResources memberResources = getMemberResources(member);
        int resourceCnt = memberResources.getResourceCnt(resourceType);
        memberResources.plusResources(resourceType, cnt);
        return resourceCnt == memberResources.getResourceCnt(resourceType);
    }

    @Override
    public boolean removeMemberResources(Member member, ResourceType resourceType, int cnt) {
        MemberResources memberResources = getMemberResources(member);
        int resourceCnt = memberResources.getResourceCnt(resourceType);
        memberResources.minusResources(resourceType, cnt);
        return resourceCnt == memberResources.getResourceCnt(resourceType);
    }
}
