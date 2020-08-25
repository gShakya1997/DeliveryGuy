package com.example.deliveryguy.activities.dashboards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.models.DeliveryPerson;
import com.example.deliveryguy.models.DeliveryPersonLocation;
import com.example.deliveryguy.services.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class DeliveryPersonDashboardActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout_delivery_person;
    private NavigationView main_delivery_person_navigation_view;
    private ImageView ivShowDeliveryPersonMenu;
    private LinearLayout main_content_delivery_person;
    private GoogleMap googleMapDeliveryPerson;

    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final float END_SCALE = 0.7f;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Boolean locationPermissionGranted = false;
    private DeliveryPersonLocation deliveryPersonLocation;
    private String currentDeliveryPersonPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_person_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding();
        SharedPreferences sharedPreferences = this.getSharedPreferences("currentUserDetail", Context.MODE_PRIVATE);
        currentDeliveryPersonPhoneNo = sharedPreferences.getString("deliveryPersonPhoneNo", null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (locationPermissionGranted) {
            initializeMap();
        } else {
            getLocationPermission();
        }
        navigationDrawer();
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.deliveryPersonMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMapDeliveryPerson = googleMap;

        if (locationPermissionGranted) {
            getDeliveryPersonDetail();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMapDeliveryPerson.setMyLocationEnabled(true);
            googleMapDeliveryPerson.getUiSettings().setMyLocationButtonEnabled(true);
            googleMapDeliveryPerson.getUiSettings().setZoomControlsEnabled(true);
            googleMapDeliveryPerson.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (locationPermissionGranted) {
                    initializeMap();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void getDeliveryPersonDetail() {
        if (deliveryPersonLocation == null) {
            deliveryPersonLocation = new DeliveryPersonLocation();
            DocumentReference deliveryPersonDocumentReference = firebaseFirestore.collection("delivery_person").document(currentDeliveryPersonPhoneNo);
            deliveryPersonDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DeliveryPerson deliveryPerson = task.getResult().toObject(DeliveryPerson.class);
                        deliveryPersonLocation.setDeliveryPerson(deliveryPerson);
                        getDeviceLocation();
                    }
                }
            });
        } else {
            getDeviceLocation();
        }
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    LatLng currentLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    CameraUpdate zoom;
                    zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);
                    googleMapDeliveryPerson.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    googleMapDeliveryPerson.animateCamera(zoom);
                    googleMapDeliveryPerson.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.navigatordeliveryperson)));
                    deliveryPersonLocation.setGeoPoint(geoPoint);
                    deliveryPersonLocation.setTimeStamp(null);
                    saveDeliveryPersonLocation();
                    startLocationService();
                }
            }
        });
    }

    private void saveDeliveryPersonLocation() {
        if (deliveryPersonLocation != null) {
            DocumentReference documentReference = firebaseFirestore.collection("delivery_person_location").document(currentDeliveryPersonPhoneNo);
            documentReference.set(deliveryPersonLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        System.out.println(deliveryPersonLocation.getGeoPoint().getLatitude());
                    }
                }
            });
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            initializeMap();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (locationPermissionGranted) {
                initializeMap();
            }
        } else {
            getLocationPermission();
        }
    }

    private boolean checkMapServices() {
        if (isServiceOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private boolean isMapsEnabled() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGPS();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGPS() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGPSIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private boolean isServiceOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DeliveryPersonDashboardActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DeliveryPersonDashboardActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void navigationDrawer() {
        main_delivery_person_navigation_view.bringToFront();
        main_delivery_person_navigation_view.setNavigationItemSelectedListener(this);
        main_delivery_person_navigation_view.setCheckedItem(R.id.nav_delivery_person_home);

        ivShowDeliveryPersonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_layout_delivery_person.isDrawerVisible(GravityCompat.START))
                    drawer_layout_delivery_person.closeDrawer(GravityCompat.START);
                else drawer_layout_delivery_person.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawer_layout_delivery_person.setScrimColor(Color.TRANSPARENT);
        drawer_layout_delivery_person.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                main_content_delivery_person.setScaleX(offsetScale);
                main_content_delivery_person.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = main_content_delivery_person.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                main_content_delivery_person.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout_delivery_person.isDrawerVisible(GravityCompat.START))
            drawer_layout_delivery_person.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DeliveryPersonDashboardActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.deliveryguy.services.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void binding() {
        drawer_layout_delivery_person = findViewById(R.id.drawer_layout_delivery_person);
        main_delivery_person_navigation_view = findViewById(R.id.main_delivery_person_navigation_view);
        ivShowDeliveryPersonMenu = findViewById(R.id.ivShowDeliveryPersonMenu);
        main_content_delivery_person = findViewById(R.id.main_content_delivery_person);
    }
}