package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TrackerUpdateRequest {

    @JsonProperty("tracker_data")
    private List<TrackerUpdateItem> trackerData;

    public TrackerUpdateRequest(TrackerUpdateItem item) {
        this.trackerData = List.of(item);
    }

    public TrackerUpdateRequest(List<TrackerUpdateItem> trackerData) {
        this.trackerData = trackerData;
    }

    public List<TrackerUpdateItem> getTrackerData() {
        return trackerData;
    }

    public void setTrackerData(List<TrackerUpdateItem> trackerData) {
        this.trackerData = trackerData;
    }
}
