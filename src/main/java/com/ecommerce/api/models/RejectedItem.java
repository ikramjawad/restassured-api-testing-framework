package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RejectedItem {

    private String name;
    private String status;

    @JsonProperty("issue_with_reason")
    private List<String> issueWithReason;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getIssueWithReason() {
        return issueWithReason;
    }

    public void setIssueWithReason(List<String> issueWithReason) {
        this.issueWithReason = issueWithReason;
    }
}
