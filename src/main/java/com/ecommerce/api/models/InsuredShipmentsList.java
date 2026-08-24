package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipmentsList {

    private List<InsuredShipmentSummary> shipments;

    public List<InsuredShipmentSummary> getShipments() {
        return shipments;
    }

    public void setShipments(List<InsuredShipmentSummary> shipments) {
        this.shipments = shipments;
    }
}
