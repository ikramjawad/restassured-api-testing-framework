package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceDetails {

    private InsuredShipment shipment;

    public InsuredShipment getShipment() {
        return shipment;
    }

    public void setShipment(InsuredShipment shipment) {
        this.shipment = shipment;
    }
}
