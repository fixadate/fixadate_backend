package com.fixadate.domain.colortype.entity;

import java.util.List;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.colortype.dto.request.ColorTypeUpdateRequest;
import com.fixadate.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private boolean isDefault;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "colorType")
	private List<Adate> adates;

	public void updateColorType(ColorTypeUpdateRequest colorTypeUpdateRequest) {
		if (colorTypeUpdateRequest.newName() != null && !colorTypeUpdateRequest.newName().isEmpty()) {
			this.name = colorTypeUpdateRequest.newName();
		}
		if (colorTypeUpdateRequest.newColor() != null && !colorTypeUpdateRequest.newColor().isEmpty()) {
			this.color = colorTypeUpdateRequest.newColor();
		}
	}
}