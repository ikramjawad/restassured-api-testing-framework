package com.ecommerce.api.models;

/**
 * Every field besides tracking_number is independently optional and falls back to the value
 * already saved on the account profile when omitted - so builder methods are only called for
 * the fields a given test actually wants to send, leaving the rest genuinely absent (not blank)
 * in the request.
 */
public class InsuranceFilingRequest {

    private String trackingNumber;
    private String packageValue;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;

    public InsuranceFilingRequest trackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        return this;
    }

    public InsuranceFilingRequest packageValue(String packageValue) {
        this.packageValue = packageValue;
        return this;
    }

    public InsuranceFilingRequest firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public InsuranceFilingRequest lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public InsuranceFilingRequest addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public InsuranceFilingRequest addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public InsuranceFilingRequest city(String city) {
        this.city = city;
        return this;
    }

    public InsuranceFilingRequest state(String state) {
        this.state = state;
        return this;
    }

    public InsuranceFilingRequest country(String country) {
        this.country = country;
        return this;
    }

    public InsuranceFilingRequest zip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getZip() {
        return zip;
    }
}
