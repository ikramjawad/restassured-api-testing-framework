package com.ecommerce.api.tests.auth;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.LoginRequest;
import com.ecommerce.api.models.LoginResponse;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class AuthTests extends BaseTest {

    private static final String VALID_USERNAME = "emilys";
    private static final String VALID_PASSWORD = "emilyspass";

    @Test
    public void login_withValidCredentials_returnsAccessToken() {
        LoginRequest loginRequest = new LoginRequest(VALID_USERNAME, VALID_PASSWORD);

        LoginResponse response = given()
                .spec(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class);

        assertEquals(response.getUsername(), VALID_USERNAME);
        assertNotNull(response.getAccessToken(), "Access token should be present on successful login");
    }

    @Test
    public void login_withInvalidCredentials_returnsUnauthorized() {
        LoginRequest loginRequest = new LoginRequest(VALID_USERNAME, "wrongPassword123");

        given()
                .spec(requestSpec)
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(anyOf(is(400), is(401)));
    }
}
