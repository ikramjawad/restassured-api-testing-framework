package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Which fields must be set vs. left null depends on which update is being performed
 * (reservation update, purchase create/update, shipment create/update) - see the factory
 * methods below, each of which mirrors one documented rule set exactly.
 */
public class TrackerUpdateItem {

    @JsonProperty("reserve_id")
    private String reserveId;

    @JsonProperty("purchase_id")
    private String purchaseId;

    @JsonProperty("shipment_id")
    private String shipmentId;

    private Integer qty;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    public TrackerUpdateItem() {
    }

    public static TrackerUpdateItem forReservationUpdate(String reserveId, int qty) {
        TrackerUpdateItem item = new TrackerUpdateItem();
        item.reserveId = reserveId;
        item.qty = qty;
        return item;
    }

    public static TrackerUpdateItem forPurchaseCreate(String reserveId, String orderNo, int qty) {
        TrackerUpdateItem item = new TrackerUpdateItem();
        item.reserveId = reserveId;
        item.orderNo = orderNo;
        item.qty = qty;
        return item;
    }

    public static TrackerUpdateItem forPurchaseUpdate(String reserveId, String purchaseId, String orderNo, int qty) {
        TrackerUpdateItem item = new TrackerUpdateItem();
        item.reserveId = reserveId;
        item.purchaseId = purchaseId;
        item.orderNo = orderNo;
        item.qty = qty;
        return item;
    }

    public static TrackerUpdateItem forShipmentCreate(String reserveId, String purchaseId, String orderNo,
                                                        String trackingNumber, int qty) {
        TrackerUpdateItem item = new TrackerUpdateItem();
        item.reserveId = reserveId;
        item.purchaseId = purchaseId;
        item.orderNo = orderNo;
        item.trackingNumber = trackingNumber;
        item.qty = qty;
        return item;
    }

    public static TrackerUpdateItem forShipmentUpdate(String reserveId, String purchaseId, String shipmentId,
                                                        String trackingNumber, int qty) {
        TrackerUpdateItem item = new TrackerUpdateItem();
        item.reserveId = reserveId;
        item.purchaseId = purchaseId;
        item.shipmentId = shipmentId;
        item.trackingNumber = trackingNumber;
        item.qty = qty;
        return item;
    }

    public String getReserveId() {
        return reserveId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public Integer getQty() {
        return qty;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
}
