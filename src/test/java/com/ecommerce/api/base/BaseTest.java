package com.ecommerce.api.base;

import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.reporting.ReportLoggingFilter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import java.util.Set;

public class BaseTest {

    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setUpBase() {
        RestAssured.baseURI = ConfigManager.getBaseUrl();

        // Mask the credentials in request logs - the logs end up in surefire reports, which CI
        // archives and anyone with access to the job can open. The filter and the
        // log().ifValidationFails() logging each need the list.
        Set<String> maskedHeaders = Set.of("API-KEY", "API-SECRET");
        RestAssuredConfig config = RestAssured.config()
                .logConfig(LogConfig.logConfig().blacklistHeaders(maskedHeaders));

        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setConfig(config)
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL, true, System.out, true, maskedHeaders))
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new ReportLoggingFilter());

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
