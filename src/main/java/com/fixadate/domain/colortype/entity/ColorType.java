package com.fixadate.domain.colortype.entity;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String color;

    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "colorType", fetch = FetchType.LAZY)
    private List<Adate> adates;

    public void updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest) {
        this.color = colorTypeUpdateRequest.newColor();
        this.name = colorTypeUpdateRequest.newName();
    }
}