package com.ecommerce.api.tests.products;

import com.ecommerce.api.base.BaseTest;
import com.ecommerce.api.models.Product;
import com.ecommerce.api.models.ProductsResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ProductsTests extends BaseTest {

    @Test
    public void getAllProducts_returnsSuccessAndProducts() {
        ProductsResponse response = given()
                .spec(requestSpec)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .extract()
                .as(ProductsResponse.class);

        assertFalse(response.getProducts().isEmpty(), "Products list should not be empty");
        assertTrue(response.getTotal() > 0, "Total product count should be greater than 0");
    }

    @Test
    public void getProductById_returnsMatchingProduct() {
        int productId = 1;

        Product product = given()
                .spec(requestSpec)
                .when()
                .get("/products/{id}", productId)
                .then()
                .statusCode(200)
                .extract()
                .as(Product.class);

        assertEquals(product.getId(), productId);
        assertTrue(product.getPrice() > 0, "Product price should be greater than 0");
    }

    @Test
    public void searchProducts_returnsMatchingResults() {
        String searchTerm = "phone";

        Response response = given()
                .spec(requestSpec)
                .queryParam("q", searchTerm)
                .when()
                .get("/products/search");

        response.then().statusCode(200);

        ProductsResponse searchResponse = response.as(ProductsResponse.class);
        assertTrue(searchResponse.getTotal() >= 0, "Search should return a valid total count");
    }
}
