package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipmentSummary {

    @JsonProperty("tracking_number")
    private String trackingNumber;

    private String date;

    @JsonProperty("package_value")
    private Double packageValue;

    @JsonProperty("cost_of_insurance")
    private Double costOfInsurance;

    @JsonProperty("certificate_number")
    private String certificateNumber;

    @JsonProperty("certificate_link")
    private String certificateLink;

    private String notes;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(Double packageValue) {
        this.packageValue = packageValue;
    }

    public Double getCostOfInsurance() {
        return costOfInsurance;
    }

    public void setCostOfInsurance(Double costOfInsurance) {
        this.costOfInsurance = costOfInsurance;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getCertificateLink() {
        return certificateLink;
    }

    public void setCertificateLink(String certificateLink) {
        this.certificateLink = certificateLink;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
