package com.example.deliveryguy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.registerAndLogin.forUser.LoginWithPhoneNumber;

public class UserTypeActivity extends AppCompatActivity {
    private Button btnForUser, btnForDeliveryGuy;
    private TextView tvTitle;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initialize
        btnForUser = findViewById(R.id.btnForUser);
        btnForDeliveryGuy = findViewById(R.id.btnForDeliveryGuy);
        ivLogo = findViewById(R.id.ivLogo);
        tvTitle = findViewById(R.id.tvTitle);
    }

    public void GoToUserLogin(View view) {
        Intent intent = new Intent(UserTypeActivity.this, LoginWithPhoneNumber.class);
        startActivity(intent);
        finish();
    }
}