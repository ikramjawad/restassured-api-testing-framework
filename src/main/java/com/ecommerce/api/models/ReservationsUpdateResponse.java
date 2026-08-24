package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * valid_items/invalid_items entry shape isn't documented beyond an empty-object placeholder,
 * so entries are kept as raw maps rather than a typed POJO until a real example is available.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationsUpdateResponse {

    @JsonProperty("valid_items")
    private List<Map<String, Object>> validItems;

    @JsonProperty("invalid_items")
    private List<Map<String, Object>> invalidItems;

    public List<Map<String, Object>> getValidItems() {
        return validItems;
    }

    public void setValidItems(List<Map<String, Object>> validItems) {
        this.validItems = validItems;
    }

    public List<Map<String, Object>> getInvalidItems() {
        return invalidItems;
    }

    public void setInvalidItems(List<Map<String, Object>> invalidItems) {
        this.invalidItems = invalidItems;
    }
}
