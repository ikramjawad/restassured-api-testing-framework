package com.ecommerce.api.tests.shipments;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.AcceptedItem;
import com.ecommerce.api.models.AmazonOtpEntry;
import com.ecommerce.api.models.AmazonOtpResponse;
import com.ecommerce.api.models.RejectedItem;
import com.ecommerce.api.models.ShipmentStatusEntry;
import com.ecommerce.api.models.ShipmentStatusResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ShipmentTests extends BaseTest {

    // Must point at a real Amazon shipment pending OTP - set real values in config.properties
    // once the API is reachable.
    private static final String AMAZON_OTP_TRACKING_NUMBER = ConfigManager.getFixture("shipment.amazonOtp.trackingNumber");
    private static final String AMAZON_OTP_DELIVERY_DATE = ConfigManager.getFixture("shipment.amazonOtp.deliveryDate"); // format: yyyy-MM-dd
    private static final int AMAZON_OTP_VALUE = Integer.parseInt(ConfigManager.getFixture("shipment.amazonOtp.otp"));

    // Must point at a real shipment's tracking number - set a real value in config.properties
    // once the API is reachable.
    private static final String SHIPMENT_STATUS_TRACKING_NUMBER = ConfigManager.getFixture("shipment.status.trackingNumber");

    // Must point at a real tracking number with no active deal to set aside - set real values
    // in config.properties once the API is reachable.
    private static final String DO_NOT_PROCESS_TRACKING_NUMBER = ConfigManager.getFixture("shipment.doNotProcess.trackingNumber");
    private static final String DO_NOT_PROCESS_ITEM_NAME = ConfigManager.getFixture("shipment.doNotProcess.itemName");
    private static final String DO_NOT_PROCESS_QTY = ConfigManager.getFixture("shipment.doNotProcess.qty");
    private static final String DO_NOT_PROCESS_NOTES = ConfigManager.getFixture("shipment.doNotProcess.notes");
    // Deliberately malformed - not a real fixture, this must always fail format validation.
    private static final String MALFORMED_TRACKING_NUMBER = "INVALID TRACKING NUMBER FORMAT !!!";

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void submitAmazonOtp_withValidTrackingNumberAndOtp_returnsSuccess() {
        AmazonOtpEntry entry = new AmazonOtpEntry(AMAZON_OTP_TRACKING_NUMBER, AMAZON_OTP_DELIVERY_DATE, AMAZON_OTP_VALUE);

        AmazonOtpResponse response = submitAmazonOtp(entry);

        assertFalse(response.getOtpData().isEmpty(), "otp_data should echo back the submitted entries");
        assertEquals(response.getOtpData().get(0).getTrackingNumber(), AMAZON_OTP_TRACKING_NUMBER,
                "Tracking number should match what was submitted");
    }

    @Test
    public void submitAmazonOtp_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .contentType(ContentType.URLENC)
                .formParam("otp_data[0][tracking_number]", AMAZON_OTP_TRACKING_NUMBER)
                .formParam("otp_data[0][delivery_date]", AMAZON_OTP_DELIVERY_DATE)
                .formParam("otp_data[0][otp]", AMAZON_OTP_VALUE)
                .when()
                .post("v2/shipments/otp/amazon")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    private AmazonOtpResponse submitAmazonOtp(AmazonOtpEntry... entries) {
        RequestSpecification request = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC);

        for (int i = 0; i < entries.length; i++) {
            AmazonOtpEntry entry = entries[i];
            request = request
                    .formParam("otp_data[" + i + "][tracking_number]", entry.getTrackingNumber())
                    .formParam("otp_data[" + i + "][delivery_date]", entry.getDeliveryDate())
                    .formParam("otp_data[" + i + "][otp]", entry.getOtp());
        }

        return request
                .when()
                .post("v2/shipments/otp/amazon")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Updated successfully"))
                .extract()
                .as(AmazonOtpResponse.class);
    }

    @Test
    public void retrieveShipmentStatus_withValidTrackingNumber_returnsTrackerData() {
        ShipmentStatusResponse response = given()
                .spec(requestSpec)
                .queryParam("tracking_number", SHIPMENT_STATUS_TRACKING_NUMBER)
                .when()
                .get("v2/shipments/status")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Tracker."))
                .extract()
                .as(ShipmentStatusResponse.class);

        assertFalse(response.getTrackerData().isEmpty(), "tracker_data should not be empty");

        for (ShipmentStatusEntry entry : response.getTrackerData()) {
            assertNotNull(entry.getTrackingNumber(), "Tracking number should be present");
            assertNotNull(entry.getStatus(), "Status should be present: " + entry.getTrackingNumber());
            validateAcceptedItems(entry);
            validateRejectedItems(entry);
        }
    }

    private void validateAcceptedItems(ShipmentStatusEntry entry) {
        if (entry.getAcceptedItems() == null) {
            return;
        }
        for (AcceptedItem accepted : entry.getAcceptedItems()) {
            assertNotNull(accepted.getName(), "Accepted item name should be present: " + entry.getTrackingNumber());
            assertNotNull(accepted.getStatus(), "Accepted item status should be present: " + entry.getTrackingNumber());
        }
    }

    private void validateRejectedItems(ShipmentStatusEntry entry) {
        if (entry.getRejectedItems() == null) {
            return;
        }
        for (RejectedItem rejected : entry.getRejectedItems()) {
            assertNotNull(rejected.getName(), "Rejected item name should be present: " + entry.getTrackingNumber());
            assertNotNull(rejected.getStatus(), "Rejected item status should be present: " + entry.getTrackingNumber());
            assertTrue(rejected.getIssueWithReason() != null && !rejected.getIssueWithReason().isEmpty(),
                    "Rejected item should include at least one issue reason: " + entry.getTrackingNumber());
        }
    }

    @Test
    public void retrieveShipmentStatus_withMissingTrackingNumber_returnsNotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("v2/shipments/status")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", equalTo("Tracking Number is required"));
    }

    @Test
    public void retrieveShipmentStatus_withInvalidApiKeyAndSecret_returnsBadRequest() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .queryParam("tracking_number", SHIPMENT_STATUS_TRACKING_NUMBER)
                .when()
                .get("v2/shipments/status")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void retrieveShipmentStatus_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .queryParam("tracking_number", SHIPMENT_STATUS_TRACKING_NUMBER)
                    .when()
                    .get("v2/shipments/status");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void submitAmazonOtp_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .contentType(ContentType.URLENC)
                    .formParam("otp_data[0][tracking_number]", AMAZON_OTP_TRACKING_NUMBER)
                    .formParam("otp_data[0][delivery_date]", AMAZON_OTP_DELIVERY_DATE)
                    .formParam("otp_data[0][otp]", AMAZON_OTP_VALUE)
                    .when()
                    .post("v2/shipments/otp/amazon");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }

    @Test
    public void submitDoNotProcess_withValidTrackingNumberItemNameQtyAndNotes_returnsSuccess() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("tracking_number", DO_NOT_PROCESS_TRACKING_NUMBER)
                .formParam("item_name", DO_NOT_PROCESS_ITEM_NAME)
                .formParam("qty", DO_NOT_PROCESS_QTY)
                .formParam("notes", DO_NOT_PROCESS_NOTES)
                .when()
                .post("v2/shipments/do-not-process")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Tracking successfully noted!"));
    }

    @Test
    public void submitDoNotProcess_withInvalidTrackingNumberFormat_returnsUnprocessableEntity() {
        given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("tracking_number", MALFORMED_TRACKING_NUMBER)
                .formParam("item_name", DO_NOT_PROCESS_ITEM_NAME)
                .formParam("qty", DO_NOT_PROCESS_QTY)
                .formParam("notes", DO_NOT_PROCESS_NOTES)
                .when()
                .post("v2/shipments/do-not-process")
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo("Tracking number format is invalid"));
    }

    @Test
    public void submitDoNotProcess_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .contentType(ContentType.URLENC)
                .formParam("tracking_number", DO_NOT_PROCESS_TRACKING_NUMBER)
                .formParam("item_name", DO_NOT_PROCESS_ITEM_NAME)
                .formParam("qty", DO_NOT_PROCESS_QTY)
                .formParam("notes", DO_NOT_PROCESS_NOTES)
                .when()
                .post("v2/shipments/do-not-process")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void submitDoNotProcess_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .contentType(ContentType.URLENC)
                    .formParam("tracking_number", DO_NOT_PROCESS_TRACKING_NUMBER)
                    .formParam("item_name", DO_NOT_PROCESS_ITEM_NAME)
                    .formParam("qty", DO_NOT_PROCESS_QTY)
                    .formParam("notes", DO_NOT_PROCESS_NOTES)
                    .when()
                    .post("v2/shipments/do-not-process");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
