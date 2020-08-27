package com.example.deliveryguy.models;

public class DeliveryPerson {
    private String deliveryPersonName;
    private String deliveryPersonEmail;
    private String deliveryPersonPhoneNo;
    private String deliveryPersonDOB;
    private String deliveryPersonGender;
    private String deliveryPersonID;

    public DeliveryPerson(String deliveryPersonName, String deliveryPersonEmail, String deliveryPersonPhoneNo, String deliveryPersonDOB, String deliveryPersonGender, String deliveryPersonID) {
        this.deliveryPersonName = deliveryPersonName;
        this.deliveryPersonEmail = deliveryPersonEmail;
        this.deliveryPersonPhoneNo = deliveryPersonPhoneNo;
        this.deliveryPersonDOB = deliveryPersonDOB;
        this.deliveryPersonGender = deliveryPersonGender;
        this.deliveryPersonID = deliveryPersonID;
    }

    public DeliveryPerson() {
    }

    public String getDeliveryPersonID() {
        return deliveryPersonID;
    }

    public void setDeliveryPersonID(String deliveryPersonID) {
        this.deliveryPersonID = deliveryPersonID;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getDeliveryPersonEmail() {
        return deliveryPersonEmail;
    }

    public void setDeliveryPersonEmail(String deliveryPersonEmail) {
        this.deliveryPersonEmail = deliveryPersonEmail;
    }

    public String getDeliveryPersonPhoneNo() {
        return deliveryPersonPhoneNo;
    }

    public void setDeliveryPersonPhoneNo(String deliveryPersonPhoneNo) {
        this.deliveryPersonPhoneNo = deliveryPersonPhoneNo;
    }

    public String getDeliveryPersonDOB() {
        return deliveryPersonDOB;
    }

    public void setDeliveryPersonDOB(String deliveryPersonDOB) {
        this.deliveryPersonDOB = deliveryPersonDOB;
    }

    public String getDeliveryPersonGender() {
        return deliveryPersonGender;
    }

    public void setDeliveryPersonGender(String deliveryPersonGender) {
        this.deliveryPersonGender = deliveryPersonGender;
    }
}
