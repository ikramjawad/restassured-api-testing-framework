package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipment {

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("package_cost")
    private Double packageCost;

    @JsonProperty("insured_amount")
    private Double insuredAmount;

    @JsonProperty("cert_no")
    private String certNo;

    @JsonProperty("cert_link")
    private String certLink;

    @JsonProperty("claim_no")
    private String claimNo;

    @JsonProperty("policy_no")
    private String policyNo;

    private String status;

    private List<InsuranceItem> items;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Double getPackageCost() {
        return packageCost;
    }

    public void setPackageCost(Double packageCost) {
        this.packageCost = packageCost;
    }

    public Double getInsuredAmount() {
        return insuredAmount;
    }

    public void setInsuredAmount(Double insuredAmount) {
        this.insuredAmount = insuredAmount;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCertLink() {
        return certLink;
    }

    public void setCertLink(String certLink) {
        this.certLink = certLink;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InsuranceItem> getItems() {
        return items;
    }

    public void setItems(List<InsuranceItem> items) {
        this.items = items;
    }
}
