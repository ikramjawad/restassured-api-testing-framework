package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipmentResponse {

    private String message;
    private InsuranceDetails insurance;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InsuranceDetails getInsurance() {
        return insurance;
    }

    public void setInsurance(InsuranceDetails insurance) {
        this.insurance = insurance;
    }
}
