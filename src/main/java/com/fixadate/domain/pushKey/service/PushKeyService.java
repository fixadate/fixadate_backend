package com.fixadate.domain.pushKey.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.pushKey.entity.PushKey;
import com.fixadate.domain.pushKey.repository.PushKeyRepository;
import com.fixadate.global.facade.MemberFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushKeyService {

	private final PushKeyRepository pushKeyRepository;
	private final MemberFacade memberFacade;

	@Transactional
	public void registerPushKey(String pushKey, String memberId) {
		Optional<PushKey> pushKeyOptional = findPushKeyByKey(pushKey);

		if (pushKeyOptional.isPresent()) {
			pushKeyOptional.get().compareAndChangeKey(pushKey);
			return;
		}
		generateAndRegisterPushKey(pushKey, memberId);
	}

	@Transactional
	public Optional<PushKey> findPushKeyByKey(String pushKey) {
		return pushKeyRepository.findPushKeyByPushKey(pushKey);
	}

	@Transactional
	public void generateAndRegisterPushKey(String pushKey, String memberId) {
		pushKeyRepository.save(memberFacade.getPushKeyAndRegisterMember(memberId, pushKey));
	}

}
