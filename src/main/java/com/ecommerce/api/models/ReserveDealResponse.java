package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReserveDealResponse {

    private String message;
    private ReservedDeal reservation;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReservedDeal getReservation() {
        return reservation;
    }

    public void setReservation(ReservedDeal reservation) {
        this.reservation = reservation;
    }
}
