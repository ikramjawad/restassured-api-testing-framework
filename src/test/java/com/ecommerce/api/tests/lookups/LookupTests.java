package com.ecommerce.api.tests.lookups;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.ActiveRetailersResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class LookupTests extends BaseTest {

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void retrieveActiveRetailers_withValidApiKeyAndSecret_returnsRetailerDetails() {
        ActiveRetailersResponse response = given()
                .spec(requestSpec)
                .queryParam("page_no", 1)
                .queryParam("page_size", 50)
                .when()
                .get("v2/look-ups/retailers")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Active Retailers list."))
                .extract()
                .as(ActiveRetailersResponse.class);

        assertNotNull(response.getRetailers(), "retailers should be present");
        assertNotNull(response.getRetailers().getName(), "Retailer name should be present");
        assertNotNull(response.getRetailers().getSlug(), "Retailer slug should be present");
        assertNotNull(response.getRetailers().getLink(), "Retailer link should be present");
        assertTrue(response.getRetailers().getLink().startsWith("http"),
                "Retailer link should be a valid http(s) URL: " + response.getRetailers().getLink());
    }

    @Test
    public void retrieveActiveRetailers_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/look-ups/retailers")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void retrieveActiveRetailers_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .queryParam("page_no", 1)
                    .queryParam("page_size", 50)
                    .when()
                    .get("v2/look-ups/retailers");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
