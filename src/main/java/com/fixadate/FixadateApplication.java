package com.fixadate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class FixadateApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(FixadateApplication.class);
		builder.headless(false).run(args);
//		SpringApplication.run(FixadateApplication.class, args);
	}

}
