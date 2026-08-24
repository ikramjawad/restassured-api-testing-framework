package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonOtpEntry {

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("delivery_date")
    private String deliveryDate;

    private Integer otp;

    public AmazonOtpEntry() {
    }

    public AmazonOtpEntry(String trackingNumber, String deliveryDate, int otp) {
        this.trackingNumber = trackingNumber;
        this.deliveryDate = deliveryDate;
        this.otp = otp;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}
