package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetailItem {

    private String id;
    private String name;
    private String color;
    private String model;
    private List<String> upc;

    @JsonProperty("items")
    private List<RetailerLink> retailerLinks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getUpc() {
        return upc;
    }

    public void setUpc(List<String> upc) {
        this.upc = upc;
    }

    public List<RetailerLink> getRetailerLinks() {
        return retailerLinks;
    }

    public void setRetailerLinks(List<RetailerLink> retailerLinks) {
        this.retailerLinks = retailerLinks;
    }
}
