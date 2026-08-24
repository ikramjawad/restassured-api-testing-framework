package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelPurchaseResponse {

    private String message;

    @JsonProperty("invalid_purchases")
    private List<String> invalidPurchases;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getInvalidPurchases() {
        return invalidPurchases;
    }

    public void setInvalidPurchases(List<String> invalidPurchases) {
        this.invalidPurchases = invalidPurchases;
    }
}
