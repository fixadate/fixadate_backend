package com.fixadate.domain.pushkey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.pushkey.entity.PushKey;

public interface PushKeyRepository extends JpaRepository<PushKey, Long> {

	public Optional<PushKey> findPushKeyByPushKey(String key);
}
