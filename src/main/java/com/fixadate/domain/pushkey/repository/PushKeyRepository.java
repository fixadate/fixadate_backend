package com.fixadate.domain.pushkey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.pushkey.entity.PushKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PushKeyRepository extends JpaRepository<PushKey, Long> {

	public Optional<PushKey> findPushKeyByPushKey(String key);

	boolean existsByMemberId(String memberId);

	Optional<PushKey> findByMemberId(String memberId);

	void deleteByMemberId(String memberId);

	List<PushKey> findAllByPushKey(String pushKey);
}
