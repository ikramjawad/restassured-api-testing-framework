package com.ecommerce.api.tests.deals;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.config.ConfigManager;
import com.ecommerce.api.models.DealDetail;
import com.ecommerce.api.models.DealDetailItem;
import com.ecommerce.api.models.DealDetailResponse;
import com.ecommerce.api.models.RetailerLink;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DealDetailTests extends BaseTest {

    @Test
    public void retrieveDealBySlug_withValidApiKeyAndSecret_returnsDealDetails() {
        String slug = ConfigManager.getDealSlug();

        DealDetailResponse response = given()
                .spec(requestSpec)
                .when()
                .get("v2/deals/" + slug)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", equalTo("Deal Found."))
                .extract()
                .as(DealDetailResponse.class);

        DealDetail deal = response.getDeal();
        assertNotNull(deal, "Deal should be present");
        assertEquals(deal.getSlug(), slug, "Returned deal slug should match the requested slug");
        assertNotNull(deal.getDealId(), "Deal id should be present");
        assertNotNull(deal.getDealCode(), "Deal code should be present");
        assertNotNull(deal.getTitle(), "Deal title should be present");
        assertTrue(deal.getPayoutPrice() > 0, "Payout price should be greater than 0");

        // retail_price vs payout_price relationship depends on retail_type, same rule as the deals list endpoint
        double priceDelta = Math.abs(deal.getPayoutPrice() - deal.getRetailPrice());
        if ("Below Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(deal.getPayoutPrice() <= deal.getRetailPrice(),
                    "Below Retail deal payout should not exceed retail price");
        } else if ("Above Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(deal.getPayoutPrice() >= deal.getRetailPrice(),
                    "Above Retail deal payout should not be below retail price");
        } else if ("Full Retail".equalsIgnoreCase(deal.getRetailType())) {
            assertTrue(priceDelta < 0.01, "Full Retail deal payout should equal retail price");
        }

        // items shape differs by environment: Staging returns an array, Live returns a single
        // object. DealDetailItemsDeserializer normalizes both into a List, so this always loops.
        for (DealDetailItem item : deal.getItems()) {
            assertNotNull(item.getId(), "Item id should be present");
            assertNotNull(item.getName(), "Item name should be present");
            assertFalse(item.getRetailerLinks().isEmpty(), "Retailer links should not be empty");

            for (RetailerLink link : item.getRetailerLinks()) {
                assertNotNull(link.getRetailer(), "Retailer name should be present");
                assertTrue(link.getUrl() != null && link.getUrl().startsWith("http"),
                        "Retailer link URL should be a valid http(s) URL: " + link.getUrl());
            }
        }
    }

    @Test
    public void retrieveDealBySlug_withInvalidApiKeyAndSecret_returnsUnauthorized() {
        given()
                .contentType("application/json")
                .header("API-KEY", "invalid-key")
                .header("API-SECRET", "invalid-secret")
                .when()
                .get("v2/deals/" + ConfigManager.getDealSlug())
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("API-KEY or API-SECRET incorrect"));
    }

    @Test
    public void retrieveDealBySlug_withUnknownSlug_returnsNotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("v2/deals/" + ConfigManager.getInvalidDealSlug())
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("message", equalTo("Deal is not available"));
    }
}
