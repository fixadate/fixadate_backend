package com.fixadate.config.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.fixadate.config.WithMockTestUserSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockTestUserSecurityContextFactory.class)
public @interface WithMockTestUser {

	boolean autoGenerateMember() default true;

	String id() default "1";

	String oauthId() default "Sesame_Street";

	String name() default "foo";

	String nickname() default "test";

	String birth() default "2020-01-01";

	String gender() default "M";

	String profileImg() default "profile.jpg";

	String profession() default "developer";

	String signatureColor() default "blue";

	String email() default "foo@example.com";

	String role() default "ROLE_USER";
}
