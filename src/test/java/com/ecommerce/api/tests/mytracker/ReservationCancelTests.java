package com.ecommerce.api.tests.mytracker;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.CancelReservationResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class ReservationCancelTests extends BaseTest {

    // Must point at a real reservation still in "Reserved" status - set real values in
    // config.properties once the API is reachable.
    private static final String VALID_RESERVE_ID = ConfigManager.getFixture("reservationCancel.validReserveId");
    private static final String ALREADY_CANCELLED_OR_INVALID_RESERVE_ID =
            ConfigManager.getFixture("reservationCancel.invalidOrAlreadyCancelledReserveId");

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void cancelReservation_withValidReserveId_returnsSuccess() {
        CancelReservationResponse response = cancelReservation(VALID_RESERVE_ID);

        // invalid_reservations' exact semantics (per-position null vs. a filtered list of only the
        // failures) aren't confirmed, but under either reading a successfully cancelled id should
        // never show up there as itself.
        assertFalse(response.getInvalidReservations() != null && response.getInvalidReservations().contains(VALID_RESERVE_ID),
                "The cancelled reservation id should not be reported back as invalid");
    }

    @Test
    public void cancelReservation_withInvalidOrAlreadyCancelledReserveId_returnsUnprocessableEntity() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("reserve_id[]", ALREADY_CANCELLED_OR_INVALID_RESERVE_ID)
                .when()
                .post("v2/my-tracker/reservation/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo("Not a Valid Reservation or reservation already cancelled"));
    }

    @Test
    public void cancelReservation_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .contentType(ContentType.URLENC)
                .formParam("reserve_id[]", VALID_RESERVE_ID)
                .when()
                .post("v2/my-tracker/reservation/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    private CancelReservationResponse cancelReservation(String... reserveIds) {
        RequestSpecification request = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC);

        for (String reserveId : reserveIds) {
            request = request.formParam("reserve_id[]", reserveId);
        }

        return request
                .when()
                .post("v2/my-tracker/reservation/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Reservation cancelled successfully!"))
                .extract()
                .as(CancelReservationResponse.class);
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void cancelReservation_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .contentType(ContentType.URLENC)
                    .formParam("reserve_id[]", VALID_RESERVE_ID)
                    .when()
                    .post("v2/my-tracker/reservation/cancel");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
