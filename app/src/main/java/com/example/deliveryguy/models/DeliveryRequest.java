package com.example.deliveryguy.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DeliveryRequest {
    private GeoPoint requestPoint;
    private @ServerTimestamp
    Date timeStamp;

    public DeliveryRequest(GeoPoint requestPoint, Date timeStamp) {
        this.requestPoint = requestPoint;
        this.timeStamp = timeStamp;
    }

    public DeliveryRequest() {
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
}
