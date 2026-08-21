package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyTrackerResponse {

    private String message;

    @JsonProperty("my_tracker")
    @JsonDeserialize(using = MyTrackerListDeserializer.class)
    private List<MyTrackerItem> myTracker;

    private Paging paging;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MyTrackerItem> getMyTracker() {
        return myTracker;
    }

    public void setMyTracker(List<MyTrackerItem> myTracker) {
        this.myTracker = myTracker;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
