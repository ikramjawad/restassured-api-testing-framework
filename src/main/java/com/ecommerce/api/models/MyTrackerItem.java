package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.List;

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

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("order_deadline")
    private String orderDeadline;

    @JsonProperty("amount_paid")
    private String amountPaid;

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("deal_title")
    private String dealTitle;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("note")
    private String note;

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

    @JsonProperty("tracking_deadline")
    private String trackingDeadline;

    @JsonProperty("retailer_links")
    private List<RetailerLink> retailerLinks;

    @JsonProperty("insurance_status")
    private String insuranceStatus;

    @JsonProperty("package_received")
    private Integer packageReceived;

    @JsonProperty("missed_deadline")
    private Boolean missedDeadline;

    // Defaults to empty so rows where the API omits issue_with_item entirely (rather than
    // sending null/[]) don't leave this null - IssueWithItemsDeserializer only runs when the
    // key is present.
    @JsonProperty("issue_with_item")
    @JsonDeserialize(using = IssueWithItemsDeserializer.class)
    private List<IssueWithItem> issueWithItem = Collections.emptyList();

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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderDeadline() {
        return orderDeadline;
    }

    public void setOrderDeadline(String orderDeadline) {
        this.orderDeadline = orderDeadline;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getTrackingDeadline() {
        return trackingDeadline;
    }

    public void setTrackingDeadline(String trackingDeadline) {
        this.trackingDeadline = trackingDeadline;
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

    public List<IssueWithItem> getIssueWithItem() {
        return issueWithItem;
    }

    public void setIssueWithItem(List<IssueWithItem> issueWithItem) {
        this.issueWithItem = issueWithItem;
    }
}
