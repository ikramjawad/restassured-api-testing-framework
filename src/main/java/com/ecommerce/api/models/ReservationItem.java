package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationItem {

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("model_number")
    private String modelNumber;

    private List<String> upcs;

    @JsonProperty("color_name")
    private String colorName;

    @JsonProperty("retailer_links")
    private List<RetailerLink> retailerLinks;

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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public List<String> getUpcs() {
        return upcs;
    }

    public void setUpcs(List<String> upcs) {
        this.upcs = upcs;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public List<RetailerLink> getRetailerLinks() {
        return retailerLinks;
    }

    public void setRetailerLinks(List<RetailerLink> retailerLinks) {
        this.retailerLinks = retailerLinks;
    }
}
