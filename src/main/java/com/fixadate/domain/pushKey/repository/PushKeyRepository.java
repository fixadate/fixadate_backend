package com.fixadate.domain.pushKey.repository;

import com.fixadate.domain.pushKey.entity.PushKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushKeyRepository extends JpaRepository<PushKey, Long> {

    public Optional<PushKey> findPushKeyByPushKey(String key);
}
