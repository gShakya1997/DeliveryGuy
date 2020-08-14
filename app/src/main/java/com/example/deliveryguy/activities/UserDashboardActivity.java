package com.example.deliveryguy.activities;

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
import com.google.android.material.navigation.NavigationView;

public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout;
    private NavigationView main_navigation_view;
    private ImageView ivShowMenu;
    private LinearLayout main_content;
    private static final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();
        navigationDrawer();
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

    private void initialize() {
        main_navigation_view = findViewById(R.id.main_navigation_view);
        drawer_layout = findViewById(R.id.drawer_layout);
        ivShowMenu = findViewById(R.id.ivShowMenu);
        main_content = findViewById(R.id.main_content);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerVisible(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}