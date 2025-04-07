package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "dates_coordinations")
public class DatesCoordinations extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dates_coordination_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_coordination_dates_team_id"))
	private Teams team;

	@Column(nullable = false)
	private String proponentId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CollectStatus collectStatus;

	@Column(nullable = false)
	private int minutes;

	@Column(nullable = false)
	protected LocalDateTime startsWhen;

	@Column(nullable = false)
	protected LocalDateTime endsWhen;

	private boolean alarmSent;

    public void cancel() {
		this.collectStatus = CollectStatus.CANCELLED;
    }

    public enum CollectStatus {
		COLLECTING, CHOOSING, CONFIRMED, CANCELLED
	}

	@Builder
	public DatesCoordinations(Teams team, String title, String proponentId, int minutes, LocalDateTime startsWhen, LocalDateTime endsWhen) {
		this.team = team;
		this.title = title;
		this.proponentId = proponentId;
		this.collectStatus = CollectStatus.COLLECTING;
		this.minutes = minutes;
		this.startsWhen = startsWhen;
		this.endsWhen = endsWhen;
		this.alarmSent = false;
	}

	public void completeAlarm() {
		this.alarmSent = true;
	}

	public void choose() {
		this.collectStatus = CollectStatus.CHOOSING;
	}

	public void confirm() {
		this.collectStatus = CollectStatus.CONFIRMED;
	}
}
