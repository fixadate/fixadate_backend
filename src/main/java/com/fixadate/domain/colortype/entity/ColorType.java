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

    private String color;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "colorType")
    private List<Adate> adates;

    public void updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest) {
        if (colorTypeUpdateRequest.newName() != null) {
            this.name = colorTypeUpdateRequest.newName();
        }
        this.color = colorTypeUpdateRequest.newColor();
    }
}