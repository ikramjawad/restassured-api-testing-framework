package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetailResponse {

    private String message;
    private DealDetail deal;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DealDetail getDeal() {
        return deal;
    }

    public void setDeal(DealDetail deal) {
        this.deal = deal;
    }
}
