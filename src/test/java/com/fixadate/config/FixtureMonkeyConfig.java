package com.fixadate.config;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin;
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public class FixtureMonkeyConfig {

	/*
	simpleValueJqwikMonkey는 읽을 수 있고 극단적이지 않은 값을 생성해주는 플러그인
	문자열의 길이, 숫자의 최대 값등 극단적이지 않게 값을 생성할 수 있게 도와줌
	 */
	public static FixtureMonkey simpleValueJqwikMonkey() {
		return FixtureMonkey.builder()
							.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
							.plugin(new SimpleValueJqwikPlugin().maxStringLength(10)
																.maxNumberValue(10_000)
							)
							.build();
	}

	/*
	Jackson 어노테이션을 인식하여 객체 생성 시 적용하는 플러그인
	JSON 직렬화/역직렬화 규칙을 테스트 객체 생성에 반영함
	 */
	public static FixtureMonkey jacksonMonkey() {
		return FixtureMonkey.builder()
							.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
							.plugin(new JacksonPlugin())
							.build();
	}

	/*
	@NotNull, @Size, @Min, @Max 등의 제약 조건을 인식하고 적용하는 플러그인
	생성된 객체가 유효성 검사를 통과하도록 보장
	 */
	public static FixtureMonkey jakartaValidationMonkey() {
		return FixtureMonkey.builder()
							.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
							.plugin(new JakartaValidationPlugin())
							.build();
	}

	/*
	인자가 없는 생성자와 getter 또는 setter 중 하나를 이용해 객체를 생성하는 Introspector
	entity에서는 인자가 없는 생성자와 getter가 있기 때문에 하기의 Introspector을 사용해야 함.
	 */
	public static FixtureMonkey entityMonkey() {
		return FixtureMonkey.builder()
							.plugin(new JakartaValidationPlugin())
							.objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
							.build();
	}
}
