package com.fixadate.domain.member.service.repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.MemberResources;
import com.fixadate.domain.member.entity.ResourceType;

public interface MemberResourcesRepository {
    public MemberResources getMemberResources(Member member);
    public boolean addMemberResources(Member member, ResourceType resourceType, int cnt);
    public boolean removeMemberResources(Member member, ResourceType resourceType, int cnt);
}
