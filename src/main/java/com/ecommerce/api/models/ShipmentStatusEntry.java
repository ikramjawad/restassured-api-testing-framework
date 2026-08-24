package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentStatusEntry {

    @JsonProperty("tracking_number")
    private String trackingNumber;

    private String status;

    @JsonProperty("accepted_items")
    private List<AcceptedItem> acceptedItems;

    @JsonProperty("rejected_items")
    private List<RejectedItem> rejectedItems;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AcceptedItem> getAcceptedItems() {
        return acceptedItems;
    }

    public void setAcceptedItems(List<AcceptedItem> acceptedItems) {
        this.acceptedItems = acceptedItems;
    }

    public List<RejectedItem> getRejectedItems() {
        return rejectedItems;
    }

    public void setRejectedItems(List<RejectedItem> rejectedItems) {
        this.rejectedItems = rejectedItems;
    }
}
