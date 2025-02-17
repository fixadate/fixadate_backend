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
@NoArgsConstructor
public class PushKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String memberId;

	@Column(unique = true)
	private String pushKey;

	@Column(nullable = false)
	private boolean isLogin = true;

	@Builder
	public PushKey(String memberId, String pushKey) {
		this.memberId = memberId;
		this.pushKey = pushKey;
	}

	public void compareAndChangeKey(String key) {
		if (this.pushKey.equals(key)) {
			return;
		}
		this.pushKey = key;
	}

	public void setLogin() {
		this.isLogin = true;
	}

	public void forceLogout() {
		this.isLogin = false;
	}

	public void updatePushKey(String pushKey) {
		this.pushKey = pushKey;
	}
}
