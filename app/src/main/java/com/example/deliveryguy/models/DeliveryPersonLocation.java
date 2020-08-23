package com.example.deliveryguy.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class DeliveryPersonLocation {
    private GeoPoint geoPoint;
    @ServerTimestamp
    private Date timeStamp;
    private DeliveryPerson deliveryPerson;

    public DeliveryPersonLocation(GeoPoint geoPoint, Date timeStamp, DeliveryPerson deliveryPerson) {
        this.geoPoint = geoPoint;
        this.timeStamp = timeStamp;
        this.deliveryPerson = deliveryPerson;
    }

    public DeliveryPersonLocation() {
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public DeliveryPerson getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }
}
