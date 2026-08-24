package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonOtpResponse {

    private String message;

    @JsonProperty("otp_data")
    private List<AmazonOtpEntry> otpData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AmazonOtpEntry> getOtpData() {
        return otpData;
    }

    public void setOtpData(List<AmazonOtpEntry> otpData) {
        this.otpData = otpData;
    }
}
