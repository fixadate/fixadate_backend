package com.fixadate.domain.dates.entity;

import com.fixadate.domain.common.entity.Calendar;
import com.fixadate.domain.member.entity.Member;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_dates_member_id"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member proponent;

	@Builder
	private Dates(
		final Teams team,
		final String title,
		final String notes,
		final LocalDateTime startsWhen,
		final LocalDateTime endsWhen,
		final Member proponent,
		final String calendarId
	) {
		this.team = team;
		super.title = title;
		super.calendarId = calendarId;
		this.notes = notes;
		this.startsWhen = startsWhen;
		this.endsWhen = endsWhen;
		this.proponent = proponent;
	}

	public boolean isProponent(final Member member) {
		return this.proponent.getId().equals(member.getId());
	}


	public void updateFrom(final Dates adate) {
		this.title = adate.title;
		this.notes = adate.notes;
		this.startsWhen = adate.startsWhen;
		this.endsWhen = adate.endsWhen;
	}
}
