package com.ecommerce.api.tests.reservations;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.Reservation;
import com.ecommerce.api.models.ReservationsResponse;
import com.ecommerce.api.models.ReserveDealResponse;
import com.ecommerce.api.models.ReservedDeal;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DealReserveTests extends BaseTest {

    private static final String RESERVE_PATH = "v2/deals/reserve";
    private static final String CANCEL_PATH = "v2/my-tracker/reservation/cancel";
    private static final String ACTIVE_RESERVATIONS_PATH = "v2/deal/reservations/active";

    // Must be a live deal/item that the API key in .env is allowed to reserve. Every reservation
    // this class creates is cancelled again in cancelCreatedReservations().
    private static final String DEAL_ID = ConfigManager.getFixture("reserve.dealId");
    private static final String ITEM_ID = ConfigManager.getFixture("reserve.itemId");
    private static final int ITEM_QTY = Integer.parseInt(ConfigManager.getFixture("reserve.itemQty"));

    private final List<String> createdReserveIds = new ArrayList<>();

    @Test
    public void reserveDeal_withValidDealItemAndQty_returnsReservation() {
        // If the account already holds an active reservation on this item, the cleanup step could
        // end up cancelling a real reservation, so don't touch it.
        if (findActiveReservation(r -> ITEM_ID.equals(r.getItem().getItemId())) != null) {
            throw new SkipException("An active reservation already exists for item " + ITEM_ID
                    + " - cancel it or point reserve.itemId at another item before running this test");
        }

        ReserveDealResponse response = reserve(DEAL_ID, ITEM_ID, String.valueOf(ITEM_QTY))
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Quantity reserved successfully."))
                .extract()
                .as(ReserveDealResponse.class);

        ReservedDeal reservation = response.getReservation();
        assertNotNull(reservation, "Reservation should be present");
        assertTrue(reservation.getReserveId() != null && !reservation.getReserveId().isBlank(),
                "Reserve id should be present");
        assertEquals(reservation.getDealId(), DEAL_ID, "Reserved deal id should match the requested deal");
        assertEquals(reservation.getItemId(), ITEM_ID, "Reserved item id should match the requested item");
        assertEquals(reservation.getItemQty(), ITEM_QTY, "Reserved quantity should match the requested quantity");
        assertFalse(reservation.isReservationClosed(), "A new reservation should be open");

        String reserveId = reservation.getReserveId();
        Reservation active = findActiveReservation(r -> reserveId.equals(r.getReserveId()));
        assertNotNull(active, "New reservation should be listed in active reservations: " + reserveId);
        assertEquals(active.getDealId(), DEAL_ID, "Active reservation deal id should match: " + reserveId);
        assertEquals(active.getItem().getItemId(), ITEM_ID, "Active reservation item id should match: " + reserveId);
        assertEquals(active.getReservedQuantity(), ITEM_QTY, "Active reservation quantity should match: " + reserveId);
    }

    @DataProvider
    public Object[][] missingRequiredFields() {
        String qty = String.valueOf(ITEM_QTY);
        return new Object[][]{
                {null, ITEM_ID, qty, "The deal id field is required."},
                {DEAL_ID, null, qty, "The item id field is required."},
                {DEAL_ID, ITEM_ID, null, "The item qty field is required."},
        };
    }

    @Test(dataProvider = "missingRequiredFields")
    public void reserveDeal_withMissingRequiredField_returnsUnprocessableEntity(
            String dealId, String itemId, String itemQty, String expectedMessage) {
        reserve(dealId, itemId, itemQty)
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo(expectedMessage));
    }

    @Test
    public void reserveDeal_withNonNumericQty_returnsBadRequest() {
        reserve(DEAL_ID, ITEM_ID, "abc")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("message", equalTo("The item qty field must be a number."));
    }

    @Test
    public void reserveDeal_withZeroQty_returnsQuantityError() {
        reserve(DEAL_ID, ITEM_ID, "0")
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo("Quantity reserved failed."))
                .body("invalid_items.msg", hasItem("The item quantity should be a positive number."));
    }

    @Test
    public void reserveDeal_withItemNotInDeal_returnsInvalidItemError() {
        reserve(DEAL_ID, "this-item-does-not-exist-xyz-123", String.valueOf(ITEM_QTY))
                .then()
                .log().ifValidationFails()
                .statusCode(422)
                .body("message", equalTo("Quantity reserved failed."))
                .body("invalid_items.msg", hasItem("Invalid item."));
    }

    @Test
    public void reserveDeal_withUnknownDealId_returnsNotFound() {
        reserve("this-deal-does-not-exist-xyz-123", ITEM_ID, String.valueOf(ITEM_QTY))
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", equalTo("Deal is not available"));
    }

    @Test
    public void reserveDeal_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .contentType(ContentType.URLENC)
                .formParam("deal_id", DEAL_ID)
                .formParam("item_id", ITEM_ID)
                .formParam("item_qty", ITEM_QTY)
                .when()
                .post(RESERVE_PATH)
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    /**
     * Sends a reserve request, leaving out any field passed as null. Any reservation the API
     * creates - including one a negative test unexpectedly triggers - is recorded for cleanup.
     */
    private Response reserve(String dealId, String itemId, String itemQty) {
        RequestSpecification request = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC);

        if (dealId != null) {
            request = request.formParam("deal_id", dealId);
        }
        if (itemId != null) {
            request = request.formParam("item_id", itemId);
        }
        if (itemQty != null) {
            request = request.formParam("item_qty", itemQty);
        }

        Response response = request.when().post(RESERVE_PATH);

        String contentType = response.contentType();
        if (contentType != null && contentType.contains("json")) {
            String reserveId = response.path("reservation.reserve_id");
            if (reserveId != null) {
                createdReserveIds.add(reserveId);
            }
        }
        return response;
    }

    @AfterMethod(alwaysRun = true)
    public void cancelCreatedReservations() {
        if (createdReserveIds.isEmpty()) {
            return;
        }

        List<String> reserveIds = List.copyOf(createdReserveIds);
        createdReserveIds.clear();

        RequestSpecification request = given()
                .spec(requestSpec)
                .contentType(ContentType.URLENC);
        for (String reserveId : reserveIds) {
            request = request.formParam("reserve_id[]", reserveId);
        }

        Response response = request.when().post(CANCEL_PATH);
        assertEquals(response.statusCode(), 200,
                "Could not cancel test reservations " + reserveIds + " - cancel them manually. Response: "
                        + response.asString());
    }

    private Reservation findActiveReservation(Predicate<Reservation> matcher) {
        int lastPage = 1;
        for (int page = 1; page <= lastPage; page++) {
            ReservationsResponse response = given()
                    .spec(requestSpec)
                    .queryParam("page_no", page)
                    .when()
                    .get(ACTIVE_RESERVATIONS_PATH)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("message", equalTo("Active Reservation List."))
                    .extract()
                    .as(ReservationsResponse.class);

            for (Reservation reservation : response.getReservationList()) {
                if (reservation.getItem() != null && matcher.test(reservation)) {
                    return reservation;
                }
            }
            if (response.getPaging() != null) {
                lastPage = response.getPaging().getLastPage();
            }
        }
        return null;
    }
}
