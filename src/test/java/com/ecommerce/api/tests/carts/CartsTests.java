package com.ecommerce.api.tests.carts;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.Cart;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CartsTests extends BaseTest {

    @Test
    public void getCartById_returnsCartDetails() {
        int cartId = 1;

        Cart cart = given()
                .spec(requestSpec)
                .when()
                .get("/carts/{id}", cartId)
                .then()
                .statusCode(200)
                .extract()
                .as(Cart.class);

        assertEquals(cart.getId(), cartId);
        assertTrue(cart.getTotalProducts() > 0, "Cart should contain at least one product");
    }

    @Test
    public void getAllCarts_returnsSuccess() {
        given()
                .spec(requestSpec)
                .when()
                .get("/carts")
                .then()
                .statusCode(200)
                .body("carts.size()", org.hamcrest.Matchers.greaterThan(0));
    }
}
