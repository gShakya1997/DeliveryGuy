package com.example.deliveryguy.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UsersLocation {
    private GeoPoint geoPoint;
    private @ServerTimestamp
    Date timestamp;
    private Users users;

    public UsersLocation(GeoPoint geoPoint, Date timestamp, Users users) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.users = users;
    }

    public UsersLocation() {
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
