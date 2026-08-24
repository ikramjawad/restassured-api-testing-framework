package com.ecommerce.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// This endpoint's paging shape is richer than - and inconsistent with - the current_page/
// last_page/total Paging used everywhere else in this API (it also uses page/per_page query
// params instead of page_no/page_size), so it gets its own model rather than reusing Paging.
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuredShipmentsPaging {

    @JsonProperty("current_page")
    private Integer currentPage;

    private Integer from;

    @JsonProperty("last_page")
    private Integer lastPage;

    private Integer to;
    private Integer total;

    @JsonProperty("perPage")
    private Integer perPage;

    private Integer pages;
    private Integer first;
    private Integer last;
    private String prev;
    private String next;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getLast() {
        return last;
    }

    public void setLast(Integer last) {
        this.last = last;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
