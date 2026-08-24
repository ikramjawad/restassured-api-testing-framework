package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackerUpdateResponse {

    private String message;

    @JsonProperty("reservations_response")
    private ReservationsUpdateResponse reservationsResponse;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReservationsUpdateResponse getReservationsResponse() {
        return reservationsResponse;
    }

    public void setReservationsResponse(ReservationsUpdateResponse reservationsResponse) {
        this.reservationsResponse = reservationsResponse;
    }
}
