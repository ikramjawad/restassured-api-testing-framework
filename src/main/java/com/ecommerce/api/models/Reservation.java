package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reservation {

    private ReservationItem item;

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("reserve_id")
    private String reserveId;

    @JsonProperty("reserved_quantity")
    private int reservedQuantity;

    @JsonProperty("remaining_quota")
    private int remainingQuota;

    @JsonProperty("reserved_at")
    private String reservedAt;

    @JsonProperty("expiry_after")
    private String expiryAfter;

    public ReservationItem getItem() {
        return item;
    }

    public void setItem(ReservationItem item) {
        this.item = item;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public int getRemainingQuota() {
        return remainingQuota;
    }

    public void setRemainingQuota(int remainingQuota) {
        this.remainingQuota = remainingQuota;
    }

    public String getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(String reservedAt) {
        this.reservedAt = reservedAt;
    }

    public String getExpiryAfter() {
        return expiryAfter;
    }

    public void setExpiryAfter(String expiryAfter) {
        this.expiryAfter = expiryAfter;
    }
}
