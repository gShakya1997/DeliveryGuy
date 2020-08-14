package com.example.deliveryguy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.registerAndLogin.LoginWithPhoneNumber;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SplashScreenActivity extends AppCompatActivity {
    Animation topAnim, botAnim;
    private ImageView ivLogo;
    private TextView tvAppTitle, tvSlogan;

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initialize();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        ivLogo.setAnimation(topAnim);
        tvAppTitle.setAnimation(botAnim);
        tvSlogan.setAnimation(botAnim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkUser()) {
                    Intent intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                Intent intent = new Intent(SplashScreenActivity.this, LoginWithPhoneNumber.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private boolean checkUser() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("currentUserDetail", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);
        return isLoggedIn;
    }

    private void initialize() {
        ivLogo = findViewById(R.id.ivLogo);
        tvAppTitle = findViewById(R.id.tvAppTitle);
        tvSlogan = findViewById(R.id.tvSlogan);
    }
}