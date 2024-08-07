package com.fixadate.global.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Fixadate API 명세서")
				.description("Fixadate의 API 문서입니다.")
				.version("v1"))
			.components(new Components()
				.addSecuritySchemes("bearer-key",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));

	}

}
