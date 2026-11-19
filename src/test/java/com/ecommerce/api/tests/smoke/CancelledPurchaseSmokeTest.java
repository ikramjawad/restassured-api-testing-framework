package com.ecommerce.api.tests.smoke;

import com.ecommerce.api.models.MyTrackerItem;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Smoke 3: deals API -> find deal by slug -> reserve -> submit order # on the reservation
 * -> cancel the purchase.
 */
public class CancelledPurchaseSmokeTest extends SmokeTestBase {

    @Test(description = "Check the deals API and find the deal by slug")
    public void step1_checkDealsApiAndFindDeal() {
        checkDealsApiAndFindDeal();
    }

    @Test(dependsOnMethods = "step1_checkDealsApiAndFindDeal", description = "Reserve the deal item")
    public void step2_reserveDealItem() {
        reserveDealItem();
    }

    @Test(dependsOnMethods = "step2_reserveDealItem", description = "Submit the order number on the reservation")
    public void step3_submitOrderNumber() {
        submitOrderNumber();
    }

    @Test(dependsOnMethods = "step3_submitOrderNumber", description = "Cancel the purchase")
    public void step4_cancelPurchase() {
        cancelPurchase(purchaseId);

        MyTrackerItem row = findTrackerRow(r -> purchaseId.equals(r.getPurchaseId()));
        assertNotNull(row, "Tracker should still list cancelled purchase " + purchaseId);
        assertEquals(row.getStatus(), "cancelled", "Tracker status should be cancelled for purchase " + purchaseId);
        assertNull(findActiveReservation(reserveId),
                "Cancelling the purchase should also close its reservation: " + reserveId);
    }
}
