package com.fixadate.domain.tag.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;

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
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String color;
	private String name;
	private boolean isDefault;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member member;

	@OneToMany(mappedBy = "tag")
	private List<Adate> adates;

	public void updateTag(TagUpdateRequest tagUpdateRequest) {
		if (tagUpdateRequest.newName() != null && !tagUpdateRequest.newName().isEmpty()) {
			this.name = tagUpdateRequest.newName();
		}
		if (tagUpdateRequest.newColor() != null && !tagUpdateRequest.newColor().isEmpty()) {
			this.color = tagUpdateRequest.newColor();
		}
	}
}
