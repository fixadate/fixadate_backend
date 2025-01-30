package com.fixadate.domain.auth.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
	@Column(nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
	@Enumerated(EnumType.STRING)
	private DataStatus status = DataStatus.ACTIVE;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createDate;

	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	public void restore() {
		this.status = DataStatus.ACTIVE;
	}

	public void delete() {
		this.status = DataStatus.DELETED;
	}

	public void prohibit() {
		this.status = DataStatus.BANNED;
	}

	@Getter
	public enum DataStatus {

		ACTIVE("존재"),
		DELETED("삭제"),
		BANNED("금지"),
		PENDING("대기중");

		private final String korName;

		private DataStatus(String korName) {
			this.korName = korName;
		}
	}
}
