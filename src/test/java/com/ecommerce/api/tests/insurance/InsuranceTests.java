package com.ecommerce.api.tests.insurance;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.InsuranceFilingRequest;
import com.ecommerce.api.models.InsuranceItem;
import com.ecommerce.api.models.InsuredShipment;
import com.ecommerce.api.models.InsuredShipmentResponse;
import com.ecommerce.api.models.InsuredShipmentSummary;
import com.ecommerce.api.models.InsuredShipmentsResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class InsuranceTests extends BaseTest {

    // Must point at a real shipment with no insurance filed yet - set real values in
    // config.properties once the API is reachable.
    private static final String VALID_TRACKING_NUMBER = ConfigManager.getFixture("insurance.validTrackingNumber");
    // Must point at a real shipment that already has insurance filed.
    private static final String ALREADY_INSURED_TRACKING_NUMBER = ConfigManager.getFixture("insurance.alreadyInsuredTrackingNumber");
    private static final String INVALID_TRACKING_NUMBER = ConfigManager.getFixture("insurance.invalidTrackingNumber");
    // Must point at a real shipment with insurance filed, dedicated to the void test so voiding
    // it doesn't break the tests that depend on ALREADY_INSURED_TRACKING_NUMBER staying insured.
    private static final String VOIDABLE_TRACKING_NUMBER = ConfigManager.getFixture("insurance.voidableTrackingNumber");

    private static final String PACKAGE_VALUE = ConfigManager.getFixture("insurance.packageValue");
    private static final String FIRST_NAME = ConfigManager.getFixture("insurance.firstName");
    private static final String LAST_NAME = ConfigManager.getFixture("insurance.lastName");
    private static final String ADDRESS_LINE_1 = ConfigManager.getFixture("insurance.addressLine1");
    private static final String ADDRESS_LINE_2 = ConfigManager.getFixture("insurance.addressLine2");
    private static final String CITY = ConfigManager.getFixture("insurance.city");
    private static final String STATE = ConfigManager.getFixture("insurance.state");
    private static final String COUNTRY = ConfigManager.getFixture("insurance.country");
    private static final String ZIP = ConfigManager.getFixture("insurance.zip");

    private static final int RATE_LIMIT_MAX_ATTEMPTS = 50;

    @Test
    public void fileInsurance_withAllFieldsProvided_returnsSuccess() {
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(VALID_TRACKING_NUMBER)
                .packageValue(PACKAGE_VALUE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Shipment insurance filed successfully"));
    }

    @Test
    public void fileInsurance_withOnlyTrackingNumber_reliesOnProfileFallback_returnsSuccess() {
        // Requires the test account's profile to already have a complete name/address on file.
        InsuranceFilingRequest data = new InsuranceFilingRequest().trackingNumber(VALID_TRACKING_NUMBER);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Shipment insurance filed successfully"));
    }

    @Test
    public void fileInsurance_withOnlySingleAddressFieldUpdate_returnsSuccess() {
        // Demonstrates the documented "send just address[city] to update only that field" behavior.
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(VALID_TRACKING_NUMBER)
                .city(CITY);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Shipment insurance filed successfully"));
    }

    @Test
    public void fileInsurance_withMissingTrackingNumber_returnsBadRequest() {
        // tracking_number has no profile fallback - unlike every other field, omitting it should
        // fail regardless of account state.
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .packageValue(PACKAGE_VALUE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressLine1(ADDRESS_LINE_1)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("Tracking number is required."));
    }

    @Test
    public void fileInsurance_withMissingAddressLine1AndNoProfileFallback_returnsBadRequest() {
        // Requires a test account whose profile has no address_1 on file, so omitting it here
        // has nothing to fall back to.
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(VALID_TRACKING_NUMBER)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("Address Line 1 is required."));
    }

    @Test
    public void fileInsurance_withInvalidTrackingNumber_returnsNotFound() {
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(INVALID_TRACKING_NUMBER)
                .packageValue(PACKAGE_VALUE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressLine1(ADDRESS_LINE_1)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        // Exact message isn't confirmed yet - only a generic placeholder was documented.
        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    public void fileInsurance_withAlreadyFiledShipment_returnsConflict() {
        // Insurance can only be filed once per shipment - filing again for a shipment that
        // already has insurance returns 201 instead of 200. Exact message isn't confirmed yet.
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(ALREADY_INSURED_TRACKING_NUMBER)
                .packageValue(PACKAGE_VALUE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressLine1(ADDRESS_LINE_1)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        buildInsuranceFilingRequest(data)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("message", notNullValue());
    }

    @Test
    public void fileInsurance_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .multiPart("tracking_number", VALID_TRACKING_NUMBER)
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test
    public void retrieveInsuredShipment_withValidTrackingNumber_returnsInsuranceDetails() {
        InsuredShipmentResponse response = given()
                .spec(requestSpec)
                .when()
                .get("v2/insurance/shipments/" + ALREADY_INSURED_TRACKING_NUMBER)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .as(InsuredShipmentResponse.class);

        assertNotNull(response.getInsurance(), "insurance should be present");
        InsuredShipment shipment = response.getInsurance().getShipment();
        assertNotNull(shipment, "insurance.shipment should be present");
        assertEquals(shipment.getTrackingNumber(), ALREADY_INSURED_TRACKING_NUMBER,
                "Tracking number should match what was requested");
        assertNotNull(shipment.getStatus(), "Status should be present");

        if (shipment.getItems() != null) {
            for (InsuranceItem item : shipment.getItems()) {
                assertNotNull(item.getItemId(), "Item id should be present");
                assertNotNull(item.getName(), "Item name should be present");
            }
        }
    }

    @Test
    public void retrieveInsuredShipment_withInvalidTrackingNumber_returnsNotFound() {
        // Exact message isn't confirmed yet - only a generic placeholder was documented.
        given()
                .spec(requestSpec)
                .when()
                .get("v2/insurance/shipments/" + INVALID_TRACKING_NUMBER)
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    public void retrieveInsuredShipment_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/insurance/shipments/" + ALREADY_INSURED_TRACKING_NUMBER)
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void retrieveInsuredShipment_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .when()
                    .get("v2/insurance/shipments/" + ALREADY_INSURED_TRACKING_NUMBER);

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }

    @Test
    public void voidInsurance_withValidTrackingNumber_returnsSuccess() {
        given()
                .spec(requestSpec)
                .multiPart("tracking_number", VOIDABLE_TRACKING_NUMBER)
                .when()
                .post("v2/insurance/void")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Void request submitted successfully. We'll notify you once it's reviewed."));
    }

    @Test
    public void voidInsurance_withNoInsuranceFiledOnShipment_returnsBadRequest() {
        given()
                .spec(requestSpec)
                .multiPart("tracking_number", VALID_TRACKING_NUMBER)
                .when()
                .post("v2/insurance/void")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("Insurance not found against the shipment"));
    }

    @Test
    public void voidInsurance_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .multiPart("tracking_number", VOIDABLE_TRACKING_NUMBER)
                .when()
                .post("v2/insurance/void")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void voidInsurance_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .multiPart("tracking_number", VOIDABLE_TRACKING_NUMBER)
                    .when()
                    .post("v2/insurance/void");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }

    @Test
    public void retrieveInsuredShipments_withDefaultPaging_returnsShipmentList() {
        InsuredShipmentsResponse response = given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("per_page", 20)
                .when()
                .get("v2/insurance/shipments")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .as(InsuredShipmentsResponse.class);

        assertNotNull(response.getInsurance(), "insurance should be present");
        assertNotNull(response.getInsurance().getShipments(), "insurance.shipments should be present");
        assertFalse(response.getInsurance().getShipments().isEmpty(), "insurance.shipments should not be empty");
        assertNotNull(response.getPaging(), "paging should be present");
        assertNotNull(response.getPaging().getCurrentPage(), "paging.current_page should be present");

        for (InsuredShipmentSummary shipment : response.getInsurance().getShipments()) {
            assertNotNull(shipment.getTrackingNumber(), "Tracking number should be present");
        }
    }

    @Test
    public void retrieveInsuredShipments_filteredByTrackingNumbers_returnsMatchingShipment() {
        InsuredShipmentsResponse response = given()
                .spec(requestSpec)
                .queryParam("tracking_numbers", ALREADY_INSURED_TRACKING_NUMBER)
                .when()
                .get("v2/insurance/shipments")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .as(InsuredShipmentsResponse.class);

        assertFalse(response.getInsurance().getShipments().isEmpty(),
                "insurance.shipments should contain the filtered tracking number");
        for (InsuredShipmentSummary shipment : response.getInsurance().getShipments()) {
            assertTrue(ALREADY_INSURED_TRACKING_NUMBER.equals(shipment.getTrackingNumber()),
                    "Every returned shipment should match the tracking_numbers filter: " + shipment.getTrackingNumber());
        }
    }

    @Test
    public void retrieveInsuredShipments_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/insurance/shipments")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void retrieveInsuredShipments_whenRateLimited_returnsTooManyRequests() {
        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = given()
                    .spec(requestSpec)
                    .when()
                    .get("v2/insurance/shipments");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }

    private RequestSpecification buildInsuranceFilingRequest(InsuranceFilingRequest data) {
        RequestSpecification request = given().spec(requestSpec);

        request = addMultiPartIfPresent(request, "tracking_number", data.getTrackingNumber());
        request = addMultiPartIfPresent(request, "package_value", data.getPackageValue());
        request = addMultiPartIfPresent(request, "first_name", data.getFirstName());
        request = addMultiPartIfPresent(request, "last_name", data.getLastName());
        request = addMultiPartIfPresent(request, "address[address_1]", data.getAddressLine1());
        request = addMultiPartIfPresent(request, "address[address_2]", data.getAddressLine2());
        request = addMultiPartIfPresent(request, "address[city]", data.getCity());
        request = addMultiPartIfPresent(request, "address[state]", data.getState());
        request = addMultiPartIfPresent(request, "address[country]", data.getCountry());
        request = addMultiPartIfPresent(request, "address[zip]", data.getZip());

        return request;
    }

    private RequestSpecification addMultiPartIfPresent(RequestSpecification request, String name, String value) {
        return value == null ? request : request.multiPart(name, value);
    }

    @Test(enabled = false, description = "Deliberately floods the endpoint to trigger throttling. "
            + "Excluded from normal runs - enable manually and only run against an environment "
            + "where hammering the rate limiter is acceptable.")
    public void fileInsurance_whenRateLimited_returnsTooManyRequests() {
        InsuranceFilingRequest data = new InsuranceFilingRequest()
                .trackingNumber(VALID_TRACKING_NUMBER)
                .packageValue(PACKAGE_VALUE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressLine1(ADDRESS_LINE_1)
                .city(CITY)
                .state(STATE)
                .country(COUNTRY)
                .zip(ZIP);

        Response response = null;
        for (int attempt = 0; attempt < RATE_LIMIT_MAX_ATTEMPTS; attempt++) {
            response = buildInsuranceFilingRequest(data)
                    .when()
                    .post("v2/insurance/file");

            if (response.statusCode() == 429) {
                break;
            }
        }

        assertNotNull(response, "Should have received at least one response");
        assertEquals(response.statusCode(), 429, "Expected a 429 after " + RATE_LIMIT_MAX_ATTEMPTS + " rapid requests");
        assertEquals(response.jsonPath().getString("message"), "Too Many Requests");
    }
}
