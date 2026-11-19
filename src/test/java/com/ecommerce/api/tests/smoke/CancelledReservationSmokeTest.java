package com.ecommerce.api.tests.smoke;

import com.ecommerce.api.models.MyTrackerItem;
import com.ecommerce.api.models.TrackerUpdateItem;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertNull;

/**
 * Smoke 2: deals API -> find deal by slug -> reserve -> cancel the reservation
 * -> submitting an order # on the cancelled reservation must be rejected.
 */
public class CancelledReservationSmokeTest extends SmokeTestBase {

    @Test(description = "Check the deals API and find the deal by slug")
    public void step1_checkDealsApiAndFindDeal() {
        checkDealsApiAndFindDeal();
    }

    @Test(dependsOnMethods = "step1_checkDealsApiAndFindDeal", description = "Reserve the deal item")
    public void step2_reserveDealItem() {
        reserveDealItem();
    }

    @Test(dependsOnMethods = "step2_reserveDealItem", description = "Cancel the reservation")
    public void step3_cancelReservation() {
        cancelReservation();
        assertNull(findActiveReservation(reserveId), "Cancelled reservation should no longer be active: " + reserveId);
    }

    @Test(dependsOnMethods = "step3_cancelReservation",
            description = "Submitting an order number on the cancelled reservation is rejected")
    public void step4_submitOrderNumberOnCancelledReservation_isRejected() {
        Response response = postTrackerUpdate(TrackerUpdateItem.forPurchaseCreate(reserveId, ORDER_NO, ITEM_QTY));

        MyTrackerItem purchase = findTrackerRow(r -> reserveId.equals(r.getReserveId()) && r.getPurchaseId() != null);
        if (purchase != null) {
            // Remember it so cleanup cancels the purchase this bug created.
            purchaseId = purchase.getPurchaseId();
        }
        assertNull(purchase, "No purchase should be created on cancelled reservation " + reserveId);

        // The API answers 200 for the batch as a whole and reports the rejected entry under
        // invalid_items, with the reason keyed by the offending field.
        response.then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("reservations_response.valid_items", empty())
                .body("reservations_response.invalid_items.errors.reserve_id",
                        hasItem("Not a Valid Reservation or reservation already cancelled"));
    }

    @AfterClass(alwaysRun = true)
    public void cancelPurchaseCreatedByMistake() {
        if (purchaseId != null) {
            cancelPurchase(purchaseId);
        }
    }
}
