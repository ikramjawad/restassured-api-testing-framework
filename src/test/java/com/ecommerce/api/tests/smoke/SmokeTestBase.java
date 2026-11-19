package com.ecommerce.api.tests.smoke;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.CancelPurchaseResponse;
import com.ecommerce.api.models.CancelReservationResponse;
import com.ecommerce.api.models.Deal;
import com.ecommerce.api.models.DealDetail;
import com.ecommerce.api.models.DealDetailResponse;
import com.ecommerce.api.models.DealsResponse;
import com.ecommerce.api.models.MyTrackerItem;
import com.ecommerce.api.models.MyTrackerResponse;
import com.ecommerce.api.models.Reservation;
import com.ecommerce.api.models.ReservationsResponse;
import com.ecommerce.api.models.ReserveDealResponse;
import com.ecommerce.api.models.ReservedDeal;
import com.ecommerce.api.models.TrackerUpdateItem;
import com.ecommerce.api.models.TrackerUpdateRequest;
import com.ecommerce.api.models.TrackerUpdateResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Steps shared by the smoke suites. Each smoke class chains them with dependsOnMethods, so a
 * failing step skips the rest of that flow instead of producing follow-on failures.
 *
 * The ids below are filled in as a flow progresses. TestNG creates one instance per test class,
 * so every smoke suite carries its own state.
 */
public abstract class SmokeTestBase extends BaseTest {

    protected static final String DEAL_SLUG = ConfigManager.getFixture("smoke.dealSlug");
    protected static final int ITEM_QTY = Integer.parseInt(ConfigManager.getFixture("smoke.itemQty"));
    protected static final String ORDER_NO = ConfigManager.getFixture("smoke.orderNo");

    protected String dealId;
    protected String itemId;
    protected String reserveId;
    protected String purchaseId;
    protected boolean reservationCancelled;

    /**
     * Checks GET v2/deals across every page, finds DEAL_SLUG in it, then reads the deal detail by
     * slug to get the item id to reserve.
     */
    protected void checkDealsApiAndFindDeal() {
        List<Deal> allDeals = fetchAllDeals();
        assertFalse(allDeals.isEmpty(), "Deals list should not be empty");

        Deal listed = allDeals.stream()
                .filter(deal -> DEAL_SLUG.equals(deal.getSlug()))
                .findFirst()
                .orElse(null);
        assertNotNull(listed, "Deal '" + DEAL_SLUG + "' should be in the deals list");

        DealDetail deal = given()
                .spec(requestSpec)
                .when()
                .get("v2/deals/" + DEAL_SLUG)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Deal Found."))
                .extract()
                .as(DealDetailResponse.class)
                .getDeal();

        assertEquals(deal.getDealId(), listed.getDealId(), "Deal detail and deals list should agree on the deal id");
        assertFalse(deal.getItems().isEmpty(), "Deal should have at least one item to reserve");

        dealId = deal.getDealId();
        itemId = deal.getItems().get(0).getId();
    }

    protected void reserveDealItem() {
        ReservedDeal reservation = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("deal_id", dealId)
                .formParam("item_id", itemId)
                .formParam("item_qty", ITEM_QTY)
                .when()
                .post("v2/deals/reserve")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Quantity reserved successfully."))
                .extract()
                .as(ReserveDealResponse.class)
                .getReservation();

        assertNotNull(reservation, "Reservation should be present");
        assertTrue(reservation.getReserveId() != null && !reservation.getReserveId().isBlank(),
                "Reserve id should be present");
        assertEquals(reservation.getDealId(), dealId, "Reserved deal id should match");
        assertEquals(reservation.getItemId(), itemId, "Reserved item id should match");
        assertEquals(reservation.getItemQty(), ITEM_QTY, "Reserved quantity should match");

        reserveId = reservation.getReserveId();
        assertNotNull(findActiveReservation(reserveId), "New reservation should be listed as active: " + reserveId);
    }

    /** Submits ORDER_NO against the reservation and confirms the tracker shows the resulting purchase. */
    protected void submitOrderNumber() {
        assertTrackerUpdateAccepted(postTrackerUpdate(TrackerUpdateItem.forPurchaseCreate(reserveId, ORDER_NO, ITEM_QTY)));

        MyTrackerItem row = findTrackerRow(r -> reserveId.equals(r.getReserveId()) && r.getPurchaseId() != null);
        assertNotNull(row, "Tracker should show a purchase for reserve id " + reserveId + " after submitting order " + ORDER_NO);
        assertEquals(row.getOrderId(), ORDER_NO, "Tracker row should show the submitted order number");

        purchaseId = row.getPurchaseId();
    }

    protected void cancelReservation() {
        CancelReservationResponse response = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("reserve_id[]", reserveId)
                .when()
                .post("v2/my-tracker/reservation/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Reservation cancelled successfully!"))
                .extract()
                .as(CancelReservationResponse.class);

        assertTrue(response.getInvalidReservations() == null || response.getInvalidReservations().isEmpty(),
                "Cancel should not report the reservation as invalid: " + response.getInvalidReservations());
        reservationCancelled = true;
    }

    protected CancelPurchaseResponse cancelPurchase(String purchaseIdToCancel) {
        CancelPurchaseResponse response = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC)
                .formParam("purchase_id[]", purchaseIdToCancel)
                .when()
                .post("v2/my-tracker/purchase/cancel")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Purchase cancelled successfully!"))
                .extract()
                .as(CancelPurchaseResponse.class);

        assertTrue(response.getInvalidPurchases() == null || response.getInvalidPurchases().isEmpty(),
                "Cancel should not report the purchase as invalid: " + response.getInvalidPurchases());
        return response;
    }

    protected Response postTrackerUpdate(TrackerUpdateItem item) {
        return given()
                .spec(requestSpec)
                .body(new TrackerUpdateRequest(item))
                .when()
                .post("v2/my-tracker");
    }

    protected void assertTrackerUpdateAccepted(Response response) {
        TrackerUpdateResponse body = response
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Data updated successfully!"))
                .extract()
                .as(TrackerUpdateResponse.class);

        assertNotNull(body.getReservationsResponse(), "reservations_response should be present");
        assertTrue(body.getReservationsResponse().getInvalidItems() == null
                        || body.getReservationsResponse().getInvalidItems().isEmpty(),
                "Tracker update should not report invalid items: " + body.getReservationsResponse().getInvalidItems());
    }

    protected MyTrackerItem findTrackerRow(Predicate<MyTrackerItem> matcher) {
        int lastPage = 1;
        for (int page = 1; page <= lastPage; page++) {
            MyTrackerResponse response = given()
                    .spec(requestSpec)
                    .queryParam("page_no", page)
                    .when()
                    .get("v2/my-tracker")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("message", equalTo("Tracker."))
                    .extract()
                    .as(MyTrackerResponse.class);

            for (MyTrackerItem row : response.getMyTracker()) {
                if (matcher.test(row)) {
                    return row;
                }
            }
            if (response.getPaging() != null) {
                lastPage = response.getPaging().getLastPage();
            }
        }
        return null;
    }

    protected Reservation findActiveReservation(String reserveIdToFind) {
        int lastPage = 1;
        for (int page = 1; page <= lastPage; page++) {
            ReservationsResponse response = given()
                    .spec(requestSpec)
                    .queryParam("page_no", page)
                    .when()
                    .get("v2/deal/reservations/active")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("message", equalTo("Active Reservation List."))
                    .extract()
                    .as(ReservationsResponse.class);

            for (Reservation reservation : response.getReservationList()) {
                if (reserveIdToFind.equals(reservation.getReserveId())) {
                    return reservation;
                }
            }
            if (response.getPaging() != null) {
                lastPage = response.getPaging().getLastPage();
            }
        }
        return null;
    }

    private List<Deal> fetchAllDeals() {
        List<Deal> deals = new ArrayList<>();
        int lastPage = 1;
        for (int page = 1; page <= lastPage; page++) {
            DealsResponse response = given()
                    .spec(requestSpec)
                    .queryParam("page_no", page)
                    .when()
                    .get("v2/deals")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("message", equalTo("Deals list."))
                    .extract()
                    .as(DealsResponse.class);

            deals.addAll(response.getDeals());
            lastPage = response.getPaging().getLastPage();
        }
        return deals;
    }

    /**
     * A reservation that never became a purchase (because the flow stopped early) is released so
     * reservations don't pile up across runs.
     */
    @AfterClass(alwaysRun = true)
    public void releaseUnusedReservation() {
        if (reserveId == null || purchaseId != null || reservationCancelled) {
            return;
        }
        cancelReservation();
    }
}
