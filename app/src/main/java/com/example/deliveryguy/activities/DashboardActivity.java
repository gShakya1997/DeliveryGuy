package com.example.deliveryguy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    private Button btnSignOut;
    private TextView tvProfileStoreName, tvProfileStorePhoneNo;
    private TextInputLayout etProfileUpdateStoreName, etProfileUpdateStoreEmail;
    private RadioButton rbOnline, rbOffline, rbBoth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();

        final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        HashMap<String, String> hashMapCurrentUserData = sharedPreferencesManager.getCurrentUserDetailFromSharedPreference();

        String storeName = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_NAME);
        String storePhoneNo = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_PHONE_NO);
        String storeEmail = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_EMAIL);
        String storeType = hashMapCurrentUserData.get(SharedPreferencesManager.KEY_STORE_TYPE);

        if (storeType.equals("Online")) {
            rbOnline.setChecked(true);
        } else if (storeType.equals("Offline")) {
            rbOffline.setChecked(true);
        } else {
            rbBoth.setChecked(true);
        }
        tvProfileStoreName.setText(storeName);
        tvProfileStorePhoneNo.setText(storePhoneNo);
        etProfileUpdateStoreName.getEditText().setText(storeName);
        etProfileUpdateStoreEmail.getEditText().setText(storeEmail);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("currentUserDetail", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(DashboardActivity.this, "Sign out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
            }
        });
    }

    private void initialize() {
        btnSignOut = findViewById(R.id.btnSignOut);
        tvProfileStoreName = findViewById(R.id.tvProfileStoreName);
        tvProfileStorePhoneNo = findViewById(R.id.tvProfileStorePhoneNo);
        etProfileUpdateStoreName = findViewById(R.id.etProfileUpdateStoreName);
        etProfileUpdateStoreEmail = findViewById(R.id.etProfileUpdateStoreEmail);
        rbOnline = findViewById(R.id.rbOnline);
        rbOffline = findViewById(R.id.rbOffline);
        rbBoth = findViewById(R.id.rbBoth);
    }
}