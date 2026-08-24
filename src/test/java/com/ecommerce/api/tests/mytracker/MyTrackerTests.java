package com.ecommerce.api.tests.mytracker;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.AcceptedItem;
import com.ecommerce.api.models.IssueWithItem;
import com.ecommerce.api.models.MyTrackerItem;
import com.ecommerce.api.models.MyTrackerResponse;
import com.ecommerce.api.models.RejectedItem;
import com.ecommerce.api.models.RetailerLink;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class MyTrackerTests extends BaseTest {

    @Test
    public void retrieveMyTracker_withValidApiKeyAndSecret_returnsTrackerList() {
        MyTrackerResponse firstPage = fetchMyTrackerPage(1);

        int lastPage = firstPage.getPaging().getLastPage();
        int itemCount = 0;

        for (int page = 1; page <= lastPage; page++) {
            MyTrackerResponse response = page == 1 ? firstPage : fetchMyTrackerPage(page);

            for (MyTrackerItem item : response.getMyTracker()) {
                validateTrackerItem(item);
                itemCount++;
            }
        }

        // Holds whether the earner has reservations (total > 0) or not (total == 0, my_tracker
        // comes back as "No record found" and MyTrackerListDeserializer normalizes it to []).
        assertEquals(itemCount, firstPage.getPaging().getTotal(),
                "Paging total should match the number of tracker items returned across all pages");
    }

    private MyTrackerResponse fetchMyTrackerPage(int pageNo) {
        return given()
                .spec(requestSpec)
                .queryParam("page_no", pageNo)
                .when()
                .get("v2/my-tracker")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Tracker."))
                .extract()
                .as(MyTrackerResponse.class);
    }

    private void validateTrackerItem(MyTrackerItem item) {
        assertNotNull(item.getReserveId(), "Reserve id should be present");

        // Tracker flow: reserve_id -> purchase_id (order submitted) -> shipment_id (tracking
        // submitted). Until shipment_id exists (i.e. only reserve_id, or reserve_id+purchase_id,
        // is present) nothing can have been paid out or received yet.
        if (item.getShipmentId() == null) {
            assertTrue(parseAmount(item.getAmountPaid()) <= 0,
                    "Amount paid should not be greater than 0 before shipment is created: " + item.getReserveId());
            assertTrue(parseQty(item.getQtyReceived()) <= 0,
                    "Received quantity should not be greater than 0 before shipment is created: " + item.getReserveId());
        }

        assertNotNull(item.getPayoutPrice(), "Payout price should be present: " + item.getReserveId());
        assertNotNull(item.getStatus(), "Status should be present: " + item.getReserveId());
        assertNotNull(item.getTotalPayout(), "Total payout should be present: " + item.getReserveId());
        assertNotNull(item.getDealType(), "Deal type should be present: " + item.getReserveId());

        assertNotNull(item.getItemId(), "Item id should be present: " + item.getReserveId());

        // Non-deal ledger rows (insurance fees, referral bonuses, payout corrections, etc.)
        // carry an empty deal_type and no deal_id/deal_title, unlike actual deal reservations.
        boolean isDealRow = !item.getDealType().isEmpty();
        if (isDealRow) {
            assertNotNull(item.getDealId(), "Deal id should be present: " + item.getReserveId());
            assertNotNull(item.getDealTitle(), "Deal title should be present: " + item.getReserveId());
        }

        for (RetailerLink link : item.getRetailerLinks()) {
            assertNotNull(link.getRetailer(), "Retailer name should be present: " + item.getReserveId());
            // url is legitimately absent for some retailers (e.g. Walmart); only its format
            // is validated when present.
            assertTrue(link.getUrl() == null || link.getUrl().startsWith("http"),
                    "Retailer link URL should be a valid http(s) URL: " + link.getUrl());
        }

        validateIssueWithItem(item);
    }

    private void validateIssueWithItem(MyTrackerItem item) {
        for (IssueWithItem issue : item.getIssueWithItem()) {
            if (issue.getAcceptedItems() != null) {
                for (AcceptedItem accepted : issue.getAcceptedItems()) {
                    assertNotNull(accepted.getName(), "Accepted item name should be present: " + item.getReserveId());
                    assertNotNull(accepted.getStatus(), "Accepted item status should be present: " + item.getReserveId());
                }
            }

            if (issue.getRejectedItems() != null) {
                for (RejectedItem rejected : issue.getRejectedItems()) {
                    assertNotNull(rejected.getName(), "Rejected item name should be present: " + item.getReserveId());
                    assertNotNull(rejected.getStatus(), "Rejected item status should be present: " + item.getReserveId());
                    assertTrue(rejected.getIssueWithReason() != null && !rejected.getIssueWithReason().isEmpty(),
                            "Rejected item should include at least one issue reason: " + item.getReserveId());
                }
            }
        }
    }

    private double parseAmount(String amountPaid) {
        return amountPaid == null ? 0.0 : Double.parseDouble(amountPaid);
    }

    private double parseQty(String qtyReceived) {
        return qtyReceived == null ? 0.0 : Double.parseDouble(qtyReceived);
    }

    @Test
    public void retrieveMyTracker_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/my-tracker")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }
}
