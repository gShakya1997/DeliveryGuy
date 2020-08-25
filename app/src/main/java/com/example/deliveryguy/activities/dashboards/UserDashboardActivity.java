package com.example.deliveryguy.activities.dashboards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.SplashScreenActivity;
import com.example.deliveryguy.activities.dashboards.DashboardActivity;
import com.example.deliveryguy.models.DeliveryPersonLocation;
import com.example.deliveryguy.models.DeliveryRequest;
import com.example.deliveryguy.models.Users;
import com.example.deliveryguy.models.UsersLocation;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout;
    private NavigationView main_navigation_view;
    private ImageView ivShowMenu;
    private LinearLayout main_content;
    private EditText etSearchAddress;
    private Button btnRequestDelivery;
    private GoogleMap googleMap;

    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final float END_SCALE = 0.7f;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private UsersLocation usersLocation;
    private DeliveryRequest deliveryRequest;
    private String currentUserPhoneNo;
    private Boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();
        actionButtons();

        SharedPreferences sharedPreferences = this.getSharedPreferences("currentUserDetail", Context.MODE_PRIVATE);
        currentUserPhoneNo = sharedPreferences.getString("storePhoneNo", null);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (locationPermissionGranted) {
            initializeMap();
        } else {
            getLocationPermission();
        }
        navigationDrawer();
    }
    private void actionButtons(){
        btnRequestDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRequestDeliveryDetail();
            }
        });
    }

    private void saveRequestDeliveryDetail() {
        if (deliveryRequest != null){
            DocumentReference documentReference = firebaseFirestore.collection("delivery_request").document(currentUserPhoneNo);
            documentReference.set(deliveryRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        btnRequestDelivery.setText("Requesting.........");
                    }
                }
            });
        }
    }

    private void navigationDrawer() {
        main_navigation_view.bringToFront();
        main_navigation_view.setNavigationItemSelectedListener(this);
        main_navigation_view.setCheckedItem(R.id.nav_home);

        ivShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_layout.isDrawerVisible(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                else drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawer_layout.setScrimColor(Color.TRANSPARENT);
        drawer_layout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                main_content.setScaleX(offsetScale);
                main_content.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = main_content.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                main_content.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
                break;
            case R.id.btnSignOut:
                SharedPreferences sharedPreferences = getSharedPreferences("currentUserDetail", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserDashboardActivity.this, "Sign out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerVisible(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (locationPermissionGranted) {
            getUserDetail();
            initializeSearch();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            this.googleMap = googleMap;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeSearch() {
        etSearchAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        String searchLocation = etSearchAddress.getText().toString().trim();
        Geocoder geocoder = new Geocoder(UserDashboardActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            Toast.makeText(this, "" + address.toString(), Toast.LENGTH_SHORT).show();
            System.out.println(address.toString());
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
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
                    Toast.makeText(UserDashboardActivity.this, "Lat " + geoPoint.getLatitude() + "Long " + geoPoint.getLongitude(), Toast.LENGTH_SHORT).show();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    googleMap.animateCamera(zoom);
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title("Pickup Location"));
                    usersLocation.setGeoPoint(geoPoint);
                    usersLocation.setTimestamp(null);
                    deliveryRequest = new DeliveryRequest();
                    deliveryRequest.setRequestPoint(geoPoint);
                    deliveryRequest.setTimeStamp(null);
                    saveUserLocation();
                }
            }
        });
    }

    private void saveUserLocation() {
        if (usersLocation != null) {
            DocumentReference documentReference = firebaseFirestore.collection("users_location")
                    .document(currentUserPhoneNo);
            documentReference.set(usersLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        System.out.println(usersLocation.getGeoPoint().getLatitude());
                    }
                }
            });
        }
    }

    private void getUserDetail() {
        if (usersLocation == null) {
            usersLocation = new UsersLocation();
            DocumentReference userDocumentReference = firebaseFirestore.collection("users").document(currentUserPhoneNo);
            userDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Users users = task.getResult().toObject(Users.class);
                        usersLocation.setUsers(users);
                        getDeviceLocation();
                    }
                }
            });
        } else {
            getDeviceLocation();
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

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserDashboardActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserDashboardActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initialize() {
        main_navigation_view = findViewById(R.id.main_navigation_view);
        drawer_layout = findViewById(R.id.drawer_layout);
        ivShowMenu = findViewById(R.id.ivShowMenu);
        main_content = findViewById(R.id.main_content);
        etSearchAddress = findViewById(R.id.etSearchAddress);
        btnRequestDelivery = findViewById(R.id.btnRequestDelivery);
    }
}