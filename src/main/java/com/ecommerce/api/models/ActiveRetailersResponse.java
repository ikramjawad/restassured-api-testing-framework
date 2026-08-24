package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// The documented schema types "retailers" as a plain object (not an array), and lists no
// "paging" property, even though the endpoint accepts page_size/page_no. Revisit this shape
// once the real API is reachable if that turns out to be a documentation gap.
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveRetailersResponse {

    private String message;
    private Retailer retailers;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Retailer getRetailers() {
        return retailers;
    }

    public void setRetailers(Retailer retailers) {
        this.retailers = retailers;
    }
}
