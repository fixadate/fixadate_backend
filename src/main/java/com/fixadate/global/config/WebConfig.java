package com.fixadate.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
			.allowedHeaders("*")
			.exposedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600);
	}
}