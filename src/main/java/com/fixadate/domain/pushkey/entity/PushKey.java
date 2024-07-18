package com.fixadate.domain.pushkey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String memberId;

	@Column(unique = true)
	private String pushKey;

	public void compareAndChangeKey(String key) {
		if (this.pushKey.equals(key)) {
			return;
		}
		this.pushKey = key;
	}
}
