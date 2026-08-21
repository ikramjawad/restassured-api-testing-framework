package com.ecommerce.api.base;

import com.ecommerce.api.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setUpBase() {
        RestAssured.baseURI = ConfigManager.getBaseUrl();

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter());

        String apiKey = ConfigManager.getApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            specBuilder.addHeader("API-KEY", apiKey);
        }

        String apiSecretKey = ConfigManager.getApiSecretKey();
        if (apiSecretKey != null && !apiSecretKey.isBlank()) {
            specBuilder.addHeader("API-SECRET", apiSecretKey);
        }

        requestSpec = specBuilder.build();
    }
}
