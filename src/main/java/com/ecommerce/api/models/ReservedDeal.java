package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The "reservation" object returned by POST v2/deals/reserve.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservedDeal {

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("item_id")
    private String itemId;

    // The API sends this as a string ("1"); Jackson coerces it to an int.
    @JsonProperty("item_qty")
    private int itemQty;

    @JsonProperty("reserve_id")
    private String reserveId;

    @JsonProperty("is_reservation_closed")
    private boolean reservationClosed;

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public boolean isReservationClosed() {
        return reservationClosed;
    }

    public void setReservationClosed(boolean reservationClosed) {
        this.reservationClosed = reservationClosed;
    }
}
