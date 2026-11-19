package com.ecommerce.api.tests.smoke;

import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.MyTrackerItem;
import com.ecommerce.api.models.TrackerUpdateItem;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Smoke 1: deals API -> find deal by slug -> reserve -> submit order # -> submit tracking #
 * -> insure the shipment.
 *
 * A full run leaves a real reservation, purchase, shipment and insurance filing on the account
 * behind BASE_URL.
 */
public class DealToShipmentSmokeTest extends SmokeTestBase {

    private static final String TRACKING_NUMBER = ConfigManager.getFixture("smoke.trackingNumber");

    private String shipmentId;

    @Test(description = "Check the deals API and find the deal by slug")
    public void step1_checkDealsApiAndFindDeal() {
        checkDealsApiAndFindDeal();
    }

    @Test(dependsOnMethods = "step1_checkDealsApiAndFindDeal", description = "Reserve the deal item")
    public void step2_reserveDealItem() {
        reserveDealItem();
    }

    @Test(dependsOnMethods = "step2_reserveDealItem", description = "Submit the order number")
    public void step3_submitOrderNumber() {
        submitOrderNumber();
    }

    @Test(dependsOnMethods = "step3_submitOrderNumber", description = "Submit the tracking number")
    public void step4_submitTrackingNumber() {
        assertTrackerUpdateAccepted(postTrackerUpdate(
                TrackerUpdateItem.forShipmentCreate(reserveId, purchaseId, ORDER_NO, TRACKING_NUMBER, ITEM_QTY)));

        MyTrackerItem row = findTrackerRow(r -> purchaseId.equals(r.getPurchaseId()) && r.getShipmentId() != null);
        assertNotNull(row, "Tracker should show a shipment for purchase id " + purchaseId
                + " after submitting tracking number " + TRACKING_NUMBER);
        assertEquals(row.getTrackingNumber(), TRACKING_NUMBER, "Tracker row should show the submitted tracking number");

        shipmentId = row.getShipmentId();
    }

    @Test(dependsOnMethods = "step4_submitTrackingNumber", description = "Insure the shipment")
    public void step5_insureShipment() {
        given()
                .spec(requestSpec)
                .contentType("multipart/form-data")
                .multiPart("tracking_number", TRACKING_NUMBER)
                .multiPart("package_value", ConfigManager.getFixture("insurance.packageValue"))
                .multiPart("first_name", ConfigManager.getFixture("insurance.firstName"))
                .multiPart("last_name", ConfigManager.getFixture("insurance.lastName"))
                .multiPart("address[address_1]", ConfigManager.getFixture("insurance.addressLine1"))
                .multiPart("address[address_2]", ConfigManager.getFixture("insurance.addressLine2"))
                .multiPart("address[city]", ConfigManager.getFixture("insurance.city"))
                .multiPart("address[state]", ConfigManager.getFixture("insurance.state"))
                .multiPart("address[country]", ConfigManager.getFixture("insurance.country"))
                .multiPart("address[zip]", ConfigManager.getFixture("insurance.zip"))
                .when()
                .post("v2/insurance/file")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Shipment insurance filed successfully"));
    }
}
