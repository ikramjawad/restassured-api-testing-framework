package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationsResponse {

    private String message;

    @JsonProperty("reservation_list")
    @JsonDeserialize(using = ReservationListDeserializer.class)
    private List<Reservation> reservationList;

    private ReservationsPaging paging;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    public ReservationsPaging getPaging() {
        return paging;
    }

    public void setPaging(ReservationsPaging paging) {
        this.paging = paging;
    }
}
