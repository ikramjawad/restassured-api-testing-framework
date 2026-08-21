package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetail {

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("deal_code")
    private String dealCode;

    private String title;
    private String slug;

    @JsonProperty("retail_price")
    private double retailPrice;

    @JsonProperty("payout_price")
    private double payoutPrice;

    private String retailers;

    @JsonProperty("retail_type")
    private String retailType;

    @JsonProperty("is_bundle")
    private boolean isBundle;

    @JsonProperty("is_exclusive_deal")
    private boolean isExclusiveDeal;

    @JsonDeserialize(using = DealDetailItemsDeserializer.class)
    private List<DealDetailItem> items;

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealCode() {
        return dealCode;
    }

    public void setDealCode(String dealCode) {
        this.dealCode = dealCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getPayoutPrice() {
        return payoutPrice;
    }

    public void setPayoutPrice(double payoutPrice) {
        this.payoutPrice = payoutPrice;
    }

    public String getRetailers() {
        return retailers;
    }

    public void setRetailers(String retailers) {
        this.retailers = retailers;
    }

    public String getRetailType() {
        return retailType;
    }

    public void setRetailType(String retailType) {
        this.retailType = retailType;
    }

    public boolean isBundle() {
        return isBundle;
    }

    public void setBundle(boolean bundle) {
        isBundle = bundle;
    }

    public boolean isExclusiveDeal() {
        return isExclusiveDeal;
    }

    public void setExclusiveDeal(boolean exclusiveDeal) {
        isExclusiveDeal = exclusiveDeal;
    }

    public List<DealDetailItem> getItems() {
        return items;
    }

    public void setItems(List<DealDetailItem> items) {
        this.items = items;
    }
}
