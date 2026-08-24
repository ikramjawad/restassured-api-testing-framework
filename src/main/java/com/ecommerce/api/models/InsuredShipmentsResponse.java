package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipmentsResponse {

    private String message;
    private InsuredShipmentsList insurance;
    private InsuredShipmentsPaging paging;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InsuredShipmentsList getInsurance() {
        return insurance;
    }

    public void setInsurance(InsuredShipmentsList insurance) {
        this.insurance = insurance;
    }

    public InsuredShipmentsPaging getPaging() {
        return paging;
    }

    public void setPaging(InsuredShipmentsPaging paging) {
        this.paging = paging;
    }
}
