package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelReservationResponse {

    private String message;

    @JsonProperty("invalid_reservations")
    private List<String> invalidReservations;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getInvalidReservations() {
        return invalidReservations;
    }

    public void setInvalidReservations(List<String> invalidReservations) {
        this.invalidReservations = invalidReservations;
    }
}
