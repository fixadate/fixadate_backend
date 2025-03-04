package com.fixadate.global.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

import io.swagger.v3.oas.models.security.SecurityRequirement;
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
						.bearerFormat("JWT"))
				.addSecuritySchemes("content-type",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.APIKEY)
						.in(In.HEADER)
						.name("content-type"))
				.addSecuritySchemes("Fix_Version",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.APIKEY)
						.in(In.HEADER)
						.name("Fix_Version"))
				.addSecuritySchemes("tccmobdvcd",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.APIKEY)
						.in(In.HEADER)
						.name("tccmobdvcd"))
				.addSecuritySchemes("celno",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.APIKEY)
						.in(In.HEADER)
						.name("celno"))
				.addSecuritySchemes("cache-control",
					new io.swagger.v3.oas.models.security.SecurityScheme()
						.type(Type.APIKEY)
						.in(In.HEADER)
						.name("cache-control"))
			)
			.addSecurityItem(new SecurityRequirement()
				.addList("bearer-key")
				.addList("content-type")
				.addList("Fix_Version")
				.addList("tccmobdvcd")
				.addList("celno")
				.addList("cache-control")
			);

	}

}
