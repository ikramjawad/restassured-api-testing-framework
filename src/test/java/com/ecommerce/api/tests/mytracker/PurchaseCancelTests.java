package com.ecommerce.api.tests.mytracker;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.CancelPurchaseResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class PurchaseCancelTests extends BaseTest {

    // Must point at a real, not-yet-cancelled/shipped purchase - set real values in
    // config.properties once the API is reachable.
    private static final String VALID_PURCHASE_ID = ConfigManager.getFixture("purchaseCancel.validPurchaseId");
    private static final String ALREADY_CANCELLED_OR_INVALID_PURCHASE_ID =
            ConfigManager.getFixture("purchaseCancel.invalidOrAlreadyCancelledPurchaseId");

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void cancelPurchase_withValidPurchaseId_returnsSuccess() {
        CancelPurchaseResponse response = cancelPurchase(VALID_PURCHASE_ID);

        // invalid_purchases' exact semantics (per-position null vs. a filtered list of only the
        // failures) aren't confirmed, but under either reading a successfully cancelled id should
        // never show up there as itself.
        assertFalse(response.getInvalidPurchases() != null && response.getInvalidPurchases().contains(VALID_PURCHASE_ID),
                "The cancelled purchase id should not be reported back as invalid");
    }

    @Test
    public void cancelPurchase_withInvalidOrAlreadyCancelledPurchaseId_returnsUnprocessableEntity() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("purchase_id[]", ALREADY_CANCELLED_OR_INVALID_PURCHASE_ID)
                .when()
                .post("v2/my-tracker/purchase/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo("Not a Valid Purchase or already cancelled"));
    }

    @Test
    public void cancelPurchase_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .contentType(ContentType.URLENC)
                .formParam("purchase_id[]", VALID_PURCHASE_ID)
                .when()
                .post("v2/my-tracker/purchase/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    private CancelPurchaseResponse cancelPurchase(String... purchaseIds) {
        RequestSpecification request = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC);

        for (String purchaseId : purchaseIds) {
            request = request.formParam("purchase_id[]", purchaseId);
        }

        return request
                .when()
                .post("v2/my-tracker/purchase/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Purchase cancelled successfully!"))
                .extract()
                .as(CancelPurchaseResponse.class);
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void cancelPurchase_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .contentType(ContentType.URLENC)
                    .formParam("purchase_id[]", VALID_PURCHASE_ID)
                    .when()
                    .post("v2/my-tracker/purchase/cancel");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
