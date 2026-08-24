package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentStatusResponse {

    private String message;

    @JsonProperty("tracker_data")
    private List<ShipmentStatusEntry> trackerData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ShipmentStatusEntry> getTrackerData() {
        return trackerData;
    }

    public void setTrackerData(List<ShipmentStatusEntry> trackerData) {
        this.trackerData = trackerData;
    }
}
