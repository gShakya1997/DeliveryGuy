package com.example.deliveryguy.activities.dashboards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.deliveryguy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliveryPersonDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_person_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding();
        navigationDrawer();
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

    private void binding() {
        drawer_layout_delivery_person = findViewById(R.id.drawer_layout_delivery_person);
        main_delivery_person_navigation_view = findViewById(R.id.main_delivery_person_navigation_view);
        ivShowDeliveryPersonMenu = findViewById(R.id.ivShowDeliveryPersonMenu);
        main_content_delivery_person = findViewById(R.id.main_content_delivery_person);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}