package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueWithItem {

    @JsonProperty("accepted_items")
    private List<AcceptedItem> acceptedItems;

    @JsonProperty("rejected_items")
    private List<RejectedItem> rejectedItems;

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
