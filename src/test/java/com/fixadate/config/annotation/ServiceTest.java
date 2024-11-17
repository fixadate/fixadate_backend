package com.fixadate.config.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fixadate.config.DataClearExtension;

@ExtendWith(DataClearExtension.class)
@Testcontainers
@SpringBootTest
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceTest {
}
