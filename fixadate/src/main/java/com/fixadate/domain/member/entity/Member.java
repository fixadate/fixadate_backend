package com.fixadate.domain.member.entity;

import com.fixadate.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

//import com.fixadate.fixadate.Adate.entity.Adate;
//import com.fixadate.fixadate.global.entity.BaseTimeEntity;
//import com.fixadate.fixadate.member.dto.MemberEditor;
//import com.fixadate.fixadate.memberTeam.entity.MemberTeam;

//import java.util.ArrayList;
//import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;
    @Column(unique = true, nullable = false)
    private String oauthId;
    private String oauthPlatform;
    private String refreshToken;
    private String name;
    private String profileImg;
    private String nickname;
    private Integer birth;
    private Boolean gender; //boolean to selection
    private String profession;
    private String signatureColor;
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<MemberTeam> memberTeamList = new ArrayList<>();
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Adate> adateList = new ArrayList<>();



//    public MemberEditor.MemberEditorBuilder toEditor() {
//        return MemberEditor.builder()
//                .name(name)
//                .refreshToken(refreshToken)
//                .oauthId(oauthId)
//                .oauthPlatform(oauthPlatform)
//                .profileImg(profileImg)
//                .nickname(nickname)
//                .birth(birth)
//                .gender(gender)
//                .profession(profession)
//                .signatureColor(signatureColor);
//    }
//
//
//    public void edit(MemberEditor memberEditor) {
//        name = memberEditor.getName();
//        refreshToken = memberEditor.getRefreshToken();
//        oauthId = memberEditor.getOauthId();
//        oauthPlatform = memberEditor.getOauthPlatform();
//        profileImg = memberEditor.getProfileImg();
//        nickname = memberEditor.getNickname();
//        birth = memberEditor.getBirth();
//        gender = memberEditor.getGender();
//        profession = memberEditor.getProfession();
//        signatureColor = memberEditor.getSignatureColor();
//
//    }

}

