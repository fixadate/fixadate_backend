package com.fixadate.util;

import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CreateMemberRegistRequest {

    public  static List<MemberRegistRequest> registMember() {
        List<MemberRegistRequest> memberRegistRequests = new ArrayList<>();
        MemberRegistRequest memberRegistRequest1 = new MemberRegistRequest(
                "123",
                "google",
                "hong",
                "213",
                "kevin",
                20000928,
                "male",
                "student",
                "red",
                "img",
                "hong@example.com",
                "USER"
        );

        MemberRegistRequest memberRegistRequest2 = new MemberRegistRequest(
                "2",
                "google",
                "muny",
                "314",
                "alex",
                19980512,
                "female",
                "engineer",
                "blue",
                "img",
                "muny@example.com",
                "USER"
        );

        MemberRegistRequest memberRegistRequest3 = new MemberRegistRequest(
                "3",
                "google",
                "kim",
                "415",
                "emma",
                20010320,
                "male",
                "designer",
                "green",
                "img",
                "kim@example.com",
                "USER"
        );

        MemberRegistRequest memberRegistRequest4 = new MemberRegistRequest(
                "4",
                "google",
                "karina",
                "516",
                "michael",
                19991225,
                "female",
                "developer",
                "yellow",
                "img",
                "karina@example.com",
                "USER"
        );

        MemberRegistRequest memberRegistRequest5 = new MemberRegistRequest(
                "5",
                "google",
                "down",
                "617",
                "chris",
                19921005,
                "female",
                "manager",
                "orange",
                "img",
                "down@example.com",
                "USER"
        );
        memberRegistRequests.add(memberRegistRequest1);
        memberRegistRequests.add(memberRegistRequest2);
        memberRegistRequests.add(memberRegistRequest3);
        memberRegistRequests.add(memberRegistRequest4);
        memberRegistRequests.add(memberRegistRequest5);
        return memberRegistRequests;
    }
}
