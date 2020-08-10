package com.example.deliveryguy.activities.registerAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.SplashScreenActivity;
import com.example.deliveryguy.bll.Validation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginWithPhoneNumber extends AppCompatActivity {
    private TextInputLayout etPhoneNumber;
    private CountryCodePicker countryCodeHolder;
    private ImageView logo;
    private TextView tvTitle, tvDesc;
    private Button btnContinue;
    private FirebaseAuth firebaseAuth;
    private Validation validation = new Validation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone_number);
        initialize();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean validatePhone() {
        String validatePhoneNumber = etPhoneNumber.getEditText().getEditableText().toString().trim();
        if (validation.validatePhone(validatePhoneNumber).equals("required")) {
            etPhoneNumber.setError("Required");
            return false;
        } else if (validation.validatePhone(validatePhoneNumber).equals("invalidPhone")) {
            etPhoneNumber.setError("Please enter a valid phone number");
            return false;
        } else {
            etPhoneNumber.setError(null);
            return true;
        }
    }

    private void initialize() {
        countryCodeHolder = findViewById(R.id.countryCodeHolder);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnContinue = findViewById(R.id.btnContinue);
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
    }

    public void callVerifyOTPScreen(View view) {
        if (!isConnected(this)) {
            showCustomDialog();
            return;
        }
        if (!validatePhone()) {
            return;
        }
        String phoneNumber = etPhoneNumber.getEditText().getText().toString().trim();
        String phoneNumberWithCountryCode = "+" + countryCodeHolder.getFullNumber() + phoneNumber;

        Intent intent = new Intent(getApplicationContext(), CodeVerificationActivity.class);
        intent.putExtra("PhoneNo", phoneNumberWithCountryCode);
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(logo, "logoImg");
        pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
        pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
        pairs[3] = new Pair<View, String>(btnContinue, "pageButton");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginWithPhoneNumber.this, pairs);
            startActivity(intent, activityOptions.toBundle());
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginWithPhoneNumber.this);
        alertBuilder.setMessage("Please check Internet Connection")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private boolean isConnected(LoginWithPhoneNumber loginWithPhoneNumber) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginWithPhoneNumber.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected() || mobileDataConnection != null && mobileDataConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}