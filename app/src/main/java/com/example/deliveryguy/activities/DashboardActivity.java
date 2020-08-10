package com.example.deliveryguy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    private Button btnSignOut;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btnSignOut = findViewById(R.id.btnSignOut);
        tv1 = findViewById(R.id.tv1);

        final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        HashMap<String, String> hashMapCurrentUserData = sharedPreferencesManager.getCurrentUserDetailFromSharedPreference();

        String storeName = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_NAME);
        String storePhoneNo = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_PHONE_NO);
        tv1.setText(storeName + "\n" + storePhoneNo);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("currentUserDetail",MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(DashboardActivity.this, "Sign out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
            }
        });
    }
}