package com.fixadate.domain.dates.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.common.entity.Calendar;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
	@Index(name = "team_id", columnList = "team_id"),
	@Index(name = "date_range", columnList = "member_id,startsWhen,endsWhen")
})
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"member"})
public class Dates extends Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_dates_team_id"))
	private Teams team;

	@Column(unique = true)
	private String calendarId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_dates_member_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member member;

	@Builder
	private Dates(
		final String title,
		final String notes,
		final LocalDateTime startsWhen,
		final LocalDateTime endsWhen,
		final Member member,
		final String calendarId
	) {
		super.title = title;
		this.notes = notes;
		this.startsWhen = startsWhen;
		this.endsWhen = endsWhen;
		this.member = member;
		this.calendarId = calendarId;
	}

	public boolean isOwner(final Member member) {
		return this.member.equals(member);
	}


	public void updateFrom(final Dates adate) {
		this.title = adate.title;
		this.notes = adate.notes;
		this.startsWhen = adate.startsWhen;
		this.endsWhen = adate.endsWhen;
	}
}
