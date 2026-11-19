package com.ecommerce.api.tests.deals;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.Deal;
import com.ecommerce.api.models.DealItem;
import com.ecommerce.api.models.DealsResponse;
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

public class DealsTests extends BaseTest {

    @Test
    public void retrieveDeals_withValidApiKeyAndSecret_returnsDealsList() {
        DealsResponse firstPage = fetchDealsPage(1);

        assertFalse(firstPage.getDeals().isEmpty(), "Deals list should not be empty");
        assertTrue(firstPage.getPaging().getTotal() > 0, "Paging total should be greater than 0");

        int lastPage = firstPage.getPaging().getLastPage();
        Set<String> seenDealIds = new HashSet<>();

        for (int page = 1; page <= lastPage; page++) {
            DealsResponse response = page == 1 ? firstPage : fetchDealsPage(page);

            for (Deal deal : response.getDeals()) {
                validateDeal(deal);
                assertTrue(seenDealIds.add(deal.getDealId()), "Duplicate deal id across pages: " + deal.getDealId());
            }
        }

        assertEquals(seenDealIds.size(), firstPage.getPaging().getTotal(),
                "Total deals validated across all pages should match paging.total");
    }

    private DealsResponse fetchDealsPage(int pageNo) {
        return given()
                .spec(requestSpec)
                .queryParam("page_no", pageNo)
                .when()
                .get("v2/deals")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Deals list."))
                .extract()
                .as(DealsResponse.class);
    }

    private void validateDeal(Deal deal) {
        // Required for every deal, regardless of retail_type
        assertNotNull(deal.getDealId(), "Deal id should be present");
        assertNotNull(deal.getDealCode(), "Deal code should be present: " + deal.getDealId());
        assertNotNull(deal.getTitle(), "Deal title should be present: " + deal.getDealId());
        assertNotNull(deal.getSlug(), "Deal slug should be present: " + deal.getDealId());
        assertTrue(deal.getPayoutPrice() > 0, "Payout price should be greater than 0: " + deal.getDealId());

        // retail_price vs payout_price relationship depends on retail_type:
        // Below Retail -> payout <= retail, Above Retail -> payout >= retail, Full Retail -> payout == retail
        double priceDelta = Math.abs(deal.getPayoutPrice() - deal.getRetailPrice());
        if ("Below Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(deal.getPayoutPrice() <= deal.getRetailPrice(),
                    "Below Retail deal payout should not exceed retail price: " + deal.getDealId());
        } else if ("Above Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(deal.getPayoutPrice() >= deal.getRetailPrice(),
                    "Above Retail deal payout should not be below retail price: " + deal.getDealId());
        } else if ("Full Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(priceDelta < 0.01,
                    "Full Retail deal payout should equal retail price: " + deal.getDealId());
        }

        // items is legitimately absent on some deals (e.g. single-listing deals with no variant breakdown)
        if (deal.getItems() == null) {
            return;
        }

        for (DealItem item : deal.getItems()) {
            // model, upc and retailer_links[].identifier_id are legitimately optional on this API
            assertNotNull(item.getId(), "Item id should be present: " + deal.getDealId());
            assertNotNull(item.getName(), "Item name should be present: " + deal.getDealId());
            assertNotNull(item.getColor(), "Item color should be present: " + deal.getDealId());
            assertFalse(item.getRetailerLinks().isEmpty(), "Retailer links should not be empty: " + item.getId());

            for (RetailerLink link : item.getRetailerLinks()) {
                assertNotNull(link.getRetailer(), "Retailer name should be present: " + item.getId());
                assertTrue(link.getUrl() != null && link.getUrl().startsWith("http"),
                        "Retailer link URL should be a valid http(s) URL: " + link.getUrl());
            }
        }
    }

    @Test
    public void retrieveDeals_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/deals")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }
}
