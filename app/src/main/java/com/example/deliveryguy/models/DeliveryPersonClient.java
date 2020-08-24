package com.example.deliveryguy.models;

import android.app.Application;

public class DeliveryPersonClient extends Application {
    private DeliveryPerson deliveryPerson = null;

    public DeliveryPerson getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }
}
