package com.example.deliveryguy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deliveryguy.R;
import com.example.deliveryguy.bll.Validation;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout etStoreName, etStoreEmail, etStorePassword, etStoreCPassword;
    private ImageView logo;
    private TextView tvTitle, tvDesc;
    private Button btnGoToLogin, btnRegister;
    private FirebaseAuth firebaseAuth;
    private Validation validation = new Validation();
    private String storeName, storeEmail, storePassword, storeCPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    //Validation
    private boolean validateStoreName() {
        storeName = etStoreName.getEditText().getText().toString().trim();
        if (!validation.validateStoreName(storeName)) {
            etStoreName.setError("Required");
            return false;
        } else {
            etStoreName.setError(null);
            return true;
        }
    }

    private boolean validateStoreEmail() {
        storeEmail = etStoreEmail.getEditText().getText().toString().trim();
        if (validation.validateEmail(storeEmail).equals("required")) {
            etStoreEmail.setError("Required");
            return false;
        } else if (validation.validateEmail(storeEmail).equals("invalid")) {
            etStoreEmail.setError("Invalid email");
            return false;
        } else {
            etStoreEmail.setError(null);
            return true;
        }
    }

    private boolean validateStorePassword() {
        storePassword = etStorePassword.getEditText().getText().toString().trim();
        if (validation.validatePassword(storePassword).equals("required")) {
            etStorePassword.setError("Required");
            return false;
        } else if (validation.validatePassword(storePassword).equals("weak")) {
            etStorePassword.setError("Password is too weak");
            return false;
        } else {
            etStorePassword.setError(null);
            return true;
        }
    }

    private boolean validateStoreConfirmPassword() {
        storeCPassword = etStoreCPassword.getEditText().getText().toString().trim();
        if (validation.validateConfirmPassword(storePassword, storeCPassword).equals("!Password")) {
            etStoreCPassword.setError("Password doesn't match");
            return false;
        } else if (validation.validateConfirmPassword(storePassword, storeCPassword).equals("required")) {
            etStoreCPassword.setError("Required");
            return false;
        } else {
            etStoreCPassword.setError(null);
            return true;
        }
    }

    private void initialize() {
        etStoreName = findViewById(R.id.etStoreName);
        etStoreEmail = findViewById(R.id.etStoreEmail);
        etStorePassword = findViewById(R.id.etStorePassword);
        etStoreCPassword = findViewById(R.id.etStoreCPassword);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnRegister = findViewById(R.id.btnRegister);
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
    }
}