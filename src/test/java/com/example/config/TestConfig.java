package com.example.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource("classpath:test.properties")
public class TestConfig {
    
    private static final String BASE_URL = "http://localhost:8080";

    @Bean
    public RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .setContentType("application/json")
                .build();
    }

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}