package com.fixadate.domain.member.entity;

import com.fixadate.domain.member.dto.response.MemberColorResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AdateColorTypes {

    @ElementCollection
    @CollectionTable(name = "adate_color_types", joinColumns = @JoinColumn(name = "member_id"))
    @MapKeyColumn(name = "name")
    @Column(name = "color")
    private Map<String, String> adateColorTypes = new HashMap<>();

    public void addColor(String name, String color) {
        adateColorTypes.put(name, color);
    }

    public boolean ifContainsKey(String name) {
        return adateColorTypes.containsKey(name);
    }

    public void getKeyAndValue() {
        adateColorTypes.forEach((key, value) -> {});
    }
    public List<MemberColorResponse> toMemberColorResponses() {
        return adateColorTypes.entrySet().stream()
                .map(entry -> new MemberColorResponse(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

}