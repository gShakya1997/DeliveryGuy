package com.example.deliveryguy.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DeliveryRequest {
    private GeoPoint requestPoint;
    private @ServerTimestamp
    Date timeStamp;
    private String receiverPhoneNo;
    private String deliveryInstruction;

    public DeliveryRequest() {
    }

    public DeliveryRequest(GeoPoint requestPoint, Date timeStamp, String receiverPhoneNo, String deliveryInstruction) {
        this.requestPoint = requestPoint;
        this.timeStamp = timeStamp;
        this.receiverPhoneNo = receiverPhoneNo;
        this.deliveryInstruction = deliveryInstruction;
    }

    public GeoPoint getRequestPoint() {
        return requestPoint;
    }

    public void setRequestPoint(GeoPoint requestPoint) {
        this.requestPoint = requestPoint;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getReceiverPhoneNo() {
        return receiverPhoneNo;
    }

    public void setReceiverPhoneNo(String receiverPhoneNo) {
        this.receiverPhoneNo = receiverPhoneNo;
    }

    public String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }
}
