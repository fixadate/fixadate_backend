package com.fixadate.unit.domain.pushkey.service;

import static com.fixadate.unit.domain.pushkey.fixture.PushKeyFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.fixadate.domain.pushkey.entity.PushKey;
import com.fixadate.domain.pushkey.repository.PushKeyRepository;
import com.fixadate.domain.pushkey.service.PushKeyService;
import com.fixadate.global.facade.MemberFacade;

@ExtendWith(MockitoExtension.class)
@Transactional
public class PushKeyServiceTest {

	@InjectMocks
	private PushKeyService pushKeyService;
	@Mock
	private PushKeyRepository pushKeyRepository;
	@Mock
	private MemberFacade memberFacade;

	@DisplayName("PushKey을 저장한다.")
	@Test
	void registerPushKeyTest() {
		given(pushKeyRepository.findPushKeyByPushKey(any(String.class))).willReturn(Optional.ofNullable(PUSH_KEY));

		assertDoesNotThrow(() -> pushKeyService.registerPushKey(PUSH_KEY.getPushKey(), PUSH_KEY.getMemberId()));
	}

	@DisplayName("pushKey로 PushKey 객체를 찾는다.")
	@Test
	void findPushKeyByKeyTest() {
		given(pushKeyRepository.findPushKeyByPushKey(any(String.class))).willReturn(Optional.ofNullable(PUSH_KEY));

		Optional<PushKey> pushKeyOptional = pushKeyService.findPushKeyByKey(PUSH_KEY.getPushKey());

		assertEquals(PUSH_KEY.getPushKey(), pushKeyOptional.get().getPushKey());
	}

	@DisplayName("pushKey 객체를 생성하고 저장한다.")
	@Test
	void generateAndRegisterPushKey() {
		given(memberFacade.getPushKeyAndRegisterMember(any(String.class), any())).willReturn(PUSH_KEY);
		assertDoesNotThrow(
			() -> pushKeyService.generateAndRegisterPushKey(PUSH_KEY.getPushKey(), PUSH_KEY.getMemberId()));
	}
}
