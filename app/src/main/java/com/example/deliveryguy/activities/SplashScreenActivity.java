package com.example.deliveryguy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.dashboards.DeliveryPersonDashboardActivity;
import com.example.deliveryguy.activities.dashboards.UserDashboardActivity;
import com.example.deliveryguy.activities.registerAndLogin.UsersLoginWithPhoneNumber;

public class SplashScreenActivity extends AppCompatActivity {
    Animation topAnim, botAnim;
    private ImageView ivLogo;
    private TextView tvAppTitle, tvSlogan;

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
                    Intent intent = new Intent(SplashScreenActivity.this, UserDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (checkDeliveryPerson()) {
                    Intent intent = new Intent(SplashScreenActivity.this, DeliveryPersonDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, UsersLoginWithPhoneNumber.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }

    private boolean checkUser() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("currentUserDetail", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedAsUser", false);
        return isLoggedIn;
    }

    private boolean checkDeliveryPerson() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("currentUserDetail", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedAsDeliveryPerson", false);
        return isLoggedIn;
    }

    private void initialize() {
        ivLogo = findViewById(R.id.ivLogo);
        tvAppTitle = findViewById(R.id.tvAppTitle);
        tvSlogan = findViewById(R.id.tvSlogan);
    }
}