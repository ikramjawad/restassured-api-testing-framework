package com.ecommerce.api.tests.reservations;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.Reservation;
import com.ecommerce.api.models.ReservationItem;
import com.ecommerce.api.models.ReservationsResponse;
import com.ecommerce.api.models.RetailerLink;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ReservationsTests extends BaseTest {

    @Test
    public void retrieveActiveReservations_withValidApiKeyAndSecret_returnsReservationList() {
        ReservationsResponse firstPage = fetchReservationsPage(1);

        int lastPage = firstPage.getPaging().getLastPage();
        Set<String> seenReserveIds = new HashSet<>();

        for (int page = 1; page <= lastPage; page++) {
            ReservationsResponse response = page == 1 ? firstPage : fetchReservationsPage(page);

            for (Reservation reservation : response.getReservationList()) {
                validateReservation(reservation);
                assertTrue(seenReserveIds.add(reservation.getReserveId()),
                        "Duplicate reserve id across pages: " + reservation.getReserveId());
            }
        }

        assertEquals(seenReserveIds.size(), firstPage.getPaging().getTotal(),
                "Total reservations validated across all pages should match paging.total");
    }

    private ReservationsResponse fetchReservationsPage(int pageNo) {
        return given()
                .spec(requestSpec)
                .queryParam("page_no", pageNo)
                .when()
                .get("v2/deal/reservations/active")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Active Reservation List."))
                .extract()
                .as(ReservationsResponse.class);
    }

    private void validateReservation(Reservation reservation) {
        assertNotNull(reservation.getDealId(), "Deal id should be present");
        assertNotNull(reservation.getReserveId(), "Reserve id should be present");
        assertNotNull(reservation.getReservedAt(), "Reserved at date should be present: " + reservation.getReserveId());
        assertTrue(reservation.getReservedQuantity() > 0,
                "Reserved quantity should be greater than 0: " + reservation.getReserveId());
        assertTrue(reservation.getRemainingQuota() >= 0,
                "Remaining quota should not be negative: " + reservation.getReserveId());

        ReservationItem item = reservation.getItem();
        assertNotNull(item, "Reservation item should be present: " + reservation.getReserveId());
        assertNotNull(item.getItemId(), "Item id should be present: " + reservation.getReserveId());
        assertNotNull(item.getItemName(), "Item name should be present: " + reservation.getReserveId());

        assertFalse(item.getRetailerLinks().isEmpty(), "Retailer links should not be empty: " + item.getItemId());
        for (RetailerLink link : item.getRetailerLinks()) {
            assertNotNull(link.getRetailer(), "Retailer name should be present: " + item.getItemId());
            assertTrue(link.getUrl() != null && link.getUrl().startsWith("http"),
                    "Retailer link URL should be a valid http(s) URL: " + link.getUrl());
        }
    }

    @Test
    public void retrieveActiveReservations_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/deal/reservations/active")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }
}
