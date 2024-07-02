package com.fixadate.integration.config;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.arbitraries.StringArbitrary;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.jqwik.JavaTypeArbitraryGenerator;
import com.navercorp.fixturemonkey.api.jqwik.JqwikPlugin;
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

/**
 *
 * @author yongjunhong
 * @since 2024. 6. 5.
 */
public class FixtureMonkeyConfig {
	public static FixtureMonkey fieldMonkey() {
		return FixtureMonkey.builder()
			.objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
			.plugin(
				new JqwikPlugin()
					.javaTypeArbitraryGenerator(new JavaTypeArbitraryGenerator() {
						@Override
						public StringArbitrary strings() {
							return Arbitraries.strings().alpha().ofMaxLength(10);
						}
					})
			)
			.plugin(new JakartaValidationPlugin())
			.build();
	}

	public static FixtureMonkey recodeMonkey() {
		return FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.plugin(
				new JqwikPlugin()
					.javaTypeArbitraryGenerator(new JavaTypeArbitraryGenerator() {
						@Override
						public StringArbitrary strings() {
							return Arbitraries.strings().alpha().ofMaxLength(10);
						}
					})
			)
			.plugin(new JakartaValidationPlugin())
			.build();
	}

	public static FixtureMonkey LocalDateTimeMonkey() {
		return FixtureMonkey.builder()
			.plugin(new SimpleValueJqwikPlugin())
			.build();
	}
}
