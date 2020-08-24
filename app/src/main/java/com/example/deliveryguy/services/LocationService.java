package com.example.deliveryguy.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.deliveryguy.models.DeliveryPerson;
import com.example.deliveryguy.models.DeliveryPersonClient;
import com.example.deliveryguy.models.DeliveryPersonLocation;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    private String deliveryPersonName, deliveryPersonEmail, deliveryPersonPhoneNo, deliveryPersonDOB, deliveryPersonGender;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(LocationService.this);
                            HashMap<String, String> hashMapCurrentUserData = sharedPreferencesManager.getCurrentDeliveryPersonDetailFromSharedPreference();
                            deliveryPersonName = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_DELIVERY_PERSON_NAME);
                            deliveryPersonEmail = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_DELIVERY_PERSON_EMAIL);
                            deliveryPersonPhoneNo = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_DELIVERY_PERSON_PHONE_NO);
                            deliveryPersonDOB = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_DELIVERY_PERSON_DOB);
                            deliveryPersonGender = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_DELIVERY_PERSON_GENDER);

                            DeliveryPerson deliveryPerson = new DeliveryPerson(deliveryPersonName, deliveryPersonEmail, deliveryPersonPhoneNo, deliveryPersonDOB, deliveryPersonGender);
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            DeliveryPersonLocation deliveryPersonLocation = new DeliveryPersonLocation(geoPoint, null, deliveryPerson);
                            saveUserLocation(deliveryPersonLocation);
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveUserLocation(final DeliveryPersonLocation deliveryPersonLocation) {

        try {
            DocumentReference locationRef = FirebaseFirestore.getInstance()
                    .collection("delivery_person_location")
                    .document(deliveryPersonPhoneNo);

            locationRef.set(deliveryPersonLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: \ninserted user location into database." +
                                "\n latitude: " + deliveryPersonLocation.getGeoPoint().getLatitude() +
                                "\n longitude: " + deliveryPersonLocation.getGeoPoint().getLongitude());
                    }
                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: " + e.getMessage());
            stopSelf();
        }
    }
}
