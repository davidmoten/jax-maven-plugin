package com.github.davidmoten.jaxws;
 
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private long zipcode;
    public String getLine1() {
        return line1;
    }
    public void setLine1(String line1) {
        this.line1 = line1;
    }
    public String getLine2() {
        return line2;
    }
    public void setLine2(String line2) {
        this.line2 = line2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public long getZipcode() {
        return zipcode;
    }
    public void setZipcode(long zipcode) {
        this.zipcode = zipcode;
    }   
}