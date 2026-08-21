package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * issue_with_item is deliberately not mapped: it comes back as [], null, or
 * {"accepted_items": [...]} depending on the row, and ignoreUnknown drops it safely.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyTrackerItem {

    @JsonProperty("reserve_id")
    private String reserveId;

    @JsonProperty("purchase_id")
    private String purchaseId;

    @JsonProperty("shipment_id")
    private String shipmentId;

    private String qty;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("amount_paid")
    private String amountPaid;

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("deal_title")
    private String dealTitle;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("item_name")
    private String itemName;

    private String notes;

    @JsonProperty("item_model_number")
    private String itemModelNumber;

    @JsonProperty("qty_received")
    private String qtyReceived;

    @JsonProperty("reserved_at")
    private String reservedAt;

    @JsonProperty("date_processed")
    private String dateProcessed;

    @JsonProperty("date_paid")
    private String datePaid;

    @JsonProperty("retail_price")
    private double retailPrice;

    @JsonProperty("payout_price")
    private Double payoutPrice;

    private String status;

    @JsonProperty("total_payout")
    private String totalPayout;

    @JsonProperty("deal_type")
    private String dealType;

    @JsonProperty("tracking_status")
    private String trackingStatus;

    @JsonProperty("retailer_links")
    private List<RetailerLink> retailerLinks;

    @JsonProperty("insurance_status")
    private String insuranceStatus;

    @JsonProperty("package_received")
    private Integer packageReceived;

    @JsonProperty("missed_deadline")
    private Boolean missedDeadline;

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getItemModelNumber() {
        return itemModelNumber;
    }

    public void setItemModelNumber(String itemModelNumber) {
        this.itemModelNumber = itemModelNumber;
    }

    public String getQtyReceived() {
        return qtyReceived;
    }

    public void setQtyReceived(String qtyReceived) {
        this.qtyReceived = qtyReceived;
    }

    public String getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(String reservedAt) {
        this.reservedAt = reservedAt;
    }

    public String getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(String dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getPayoutPrice() {
        return payoutPrice;
    }

    public void setPayoutPrice(Double payoutPrice) {
        this.payoutPrice = payoutPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPayout() {
        return totalPayout;
    }

    public void setTotalPayout(String totalPayout) {
        this.totalPayout = totalPayout;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public List<RetailerLink> getRetailerLinks() {
        return retailerLinks;
    }

    public void setRetailerLinks(List<RetailerLink> retailerLinks) {
        this.retailerLinks = retailerLinks;
    }

    public String getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    public Integer getPackageReceived() {
        return packageReceived;
    }

    public void setPackageReceived(Integer packageReceived) {
        this.packageReceived = packageReceived;
    }

    public Boolean getMissedDeadline() {
        return missedDeadline;
    }

    public void setMissedDeadline(Boolean missedDeadline) {
        this.missedDeadline = missedDeadline;
    }
}
