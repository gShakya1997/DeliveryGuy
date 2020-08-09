package com.example.deliveryguy.models;

public class Users {
    private String storeName, storeEmail, storePhoneNo, storeType;

    public Users(String storeName, String storeEmail, String storePhoneNo, String storeType) {
        this.storeName = storeName;
        this.storeEmail = storeEmail;
        this.storePhoneNo = storePhoneNo;
        this.storeType = storeType;
    }

    public Users() {
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePhoneNo() {
        return storePhoneNo;
    }

    public void setStorePhoneNo(String storePhoneNo) {
        this.storePhoneNo = storePhoneNo;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }
}
