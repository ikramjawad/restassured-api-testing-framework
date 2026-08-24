package com.ecommerce.api.tests.mytracker;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.TrackerUpdateItem;
import com.ecommerce.api.models.TrackerUpdateRequest;
import com.ecommerce.api.models.TrackerUpdateResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TrackerUpdateTests extends BaseTest {

    // Each fixture below must point at a reservation that is actually in the matching
    // lifecycle state (e.g. PURCHASE_CREATE_RESERVE_ID must not already have a purchase),
    // so they can't share one id - set real values in config.properties once the API is reachable.
    private static final String RESERVATION_UPDATE_RESERVE_ID = ConfigManager.getFixture("tracker.reservationUpdate.reserveId");

    private static final String PURCHASE_CREATE_RESERVE_ID = ConfigManager.getFixture("tracker.purchaseCreate.reserveId");
    private static final String PURCHASE_CREATE_ORDER_NO = ConfigManager.getFixture("tracker.purchaseCreate.orderNo");

    private static final String PURCHASE_UPDATE_RESERVE_ID = ConfigManager.getFixture("tracker.purchaseUpdate.reserveId");
    private static final String PURCHASE_UPDATE_PURCHASE_ID = ConfigManager.getFixture("tracker.purchaseUpdate.purchaseId");
    private static final String PURCHASE_UPDATE_ORDER_NO = ConfigManager.getFixture("tracker.purchaseUpdate.orderNo");

    private static final String SHIPMENT_CREATE_RESERVE_ID = ConfigManager.getFixture("tracker.shipmentCreate.reserveId");
    private static final String SHIPMENT_CREATE_PURCHASE_ID = ConfigManager.getFixture("tracker.shipmentCreate.purchaseId");
    private static final String SHIPMENT_CREATE_ORDER_NO = ConfigManager.getFixture("tracker.shipmentCreate.orderNo");
    private static final String SHIPMENT_CREATE_TRACKING_NUMBER = ConfigManager.getFixture("tracker.shipmentCreate.trackingNumber");

    private static final String SHIPMENT_UPDATE_RESERVE_ID = ConfigManager.getFixture("tracker.shipmentUpdate.reserveId");
    private static final String SHIPMENT_UPDATE_PURCHASE_ID = ConfigManager.getFixture("tracker.shipmentUpdate.purchaseId");
    private static final String SHIPMENT_UPDATE_SHIPMENT_ID = ConfigManager.getFixture("tracker.shipmentUpdate.shipmentId");
    private static final String SHIPMENT_UPDATE_TRACKING_NUMBER = ConfigManager.getFixture("tracker.shipmentUpdate.trackingNumber");

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void updateReservation_withValidReserveIdAndQty_returnsSuccess() {
        TrackerUpdateResponse response = submitTrackerUpdate(
                TrackerUpdateItem.forReservationUpdate(RESERVATION_UPDATE_RESERVE_ID, 1));

        assertUpdateSucceeded(response);
    }

    @Test
    public void createPurchase_withValidReserveIdOrderNoAndQty_returnsSuccess() {
        TrackerUpdateResponse response = submitTrackerUpdate(
                TrackerUpdateItem.forPurchaseCreate(PURCHASE_CREATE_RESERVE_ID, PURCHASE_CREATE_ORDER_NO, 1));

        assertUpdateSucceeded(response);
    }

    @Test
    public void updatePurchase_withValidReserveIdPurchaseIdOrderNoAndQty_returnsSuccess() {
        TrackerUpdateResponse response = submitTrackerUpdate(
                TrackerUpdateItem.forPurchaseUpdate(PURCHASE_UPDATE_RESERVE_ID, PURCHASE_UPDATE_PURCHASE_ID,
                        PURCHASE_UPDATE_ORDER_NO, 1));

        assertUpdateSucceeded(response);
    }

    @Test
    public void createShipment_withValidReserveIdPurchaseIdOrderNoTrackingNumberAndQty_returnsSuccess() {
        TrackerUpdateResponse response = submitTrackerUpdate(
                TrackerUpdateItem.forShipmentCreate(SHIPMENT_CREATE_RESERVE_ID, SHIPMENT_CREATE_PURCHASE_ID,
                        SHIPMENT_CREATE_ORDER_NO, SHIPMENT_CREATE_TRACKING_NUMBER, 1));

        assertUpdateSucceeded(response);
    }

    @Test
    public void updateShipment_withValidReserveIdPurchaseIdShipmentIdTrackingNumberAndQty_returnsSuccess() {
        TrackerUpdateResponse response = submitTrackerUpdate(
                TrackerUpdateItem.forShipmentUpdate(SHIPMENT_UPDATE_RESERVE_ID, SHIPMENT_UPDATE_PURCHASE_ID,
                        SHIPMENT_UPDATE_SHIPMENT_ID, SHIPMENT_UPDATE_TRACKING_NUMBER, 1));

        assertUpdateSucceeded(response);
    }

    private void assertUpdateSucceeded(TrackerUpdateResponse response) {
        assertNotNull(response.getReservationsResponse(), "reservations_response should be present");
        assertNotNull(response.getReservationsResponse().getInvalidItems(), "invalid_items should be present");
        assertTrue(response.getReservationsResponse().getInvalidItems().isEmpty(), "invalid_items should be empty");
        assertNotNull(response.getReservationsResponse().getValidItems(), "valid_items should be present");
        assertFalse(response.getReservationsResponse().getValidItems().isEmpty(),
                "valid_items should contain the submitted update");
    }

    private TrackerUpdateResponse submitTrackerUpdate(TrackerUpdateItem item) {
        return given()
                .spec(requestSpec)
                .body(new TrackerUpdateRequest(item))
                .when()
                .post("v2/my-tracker")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Data updated successfully!"))
                .extract()
                .as(TrackerUpdateResponse.class);
    }

    @Test
    public void submitTrackerUpdate_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .body(new TrackerUpdateRequest(TrackerUpdateItem.forReservationUpdate(RESERVATION_UPDATE_RESERVE_ID, 1)))
                .when()
                .post("v2/my-tracker")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test
    public void submitTrackerUpdate_withNoIdentifiersProvided_returnsBadRequest() {
        given()
                .spec(requestSpec)
                .body(new TrackerUpdateRequest(new TrackerUpdateItem()))
                .when()
                .post("v2/my-tracker")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("At least one of purchase_id, reserve_id, or shipment_id should not be empty."));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void submitTrackerUpdate_whenRateLimited_returnsTooManyRequests() {
        TrackerUpdateRequest payload = new TrackerUpdateRequest(
                TrackerUpdateItem.forReservationUpdate(RESERVATION_UPDATE_RESERVE_ID, 1));

        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .body(payload)
                    .when()
                    .post("v2/my-tracker");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
