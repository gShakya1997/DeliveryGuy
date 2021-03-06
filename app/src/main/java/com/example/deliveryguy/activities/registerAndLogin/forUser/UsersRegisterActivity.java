package com.example.deliveryguy.activities.registerAndLogin.forUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.dashboards.DashboardActivity;
import com.example.deliveryguy.bll.Validation;
import com.example.deliveryguy.models.Users;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersRegisterActivity extends AppCompatActivity {
    private ImageView logo;
    private TextView tvTitle, tvDesc;
    private TextInputLayout etStoreName, etStoreEmail;
    private Button btnRegister;
    private RadioGroup rgStoreType;
    private Validation validation = new Validation();
    private String storeName, storeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();
        actionButtons();
    }

    private void initialize() {
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        rgStoreType = findViewById(R.id.rgStoreType);
        etStoreName = findViewById(R.id.etStoreName);
        etStoreEmail = findViewById(R.id.etStoreEmail);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void actionButtons() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateStoreName() | !validateStoreEmail() | !validateStoreType()) {
                    return;
                }
                int selectStoreType = rgStoreType.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectStoreType);
                String storeType = radioButton.getText().toString().trim();
                String phoneNo = getIntent().getStringExtra("AddPhoneNo");
                String storeName = etStoreName.getEditText().getText().toString().trim();
                String storeEmail = etStoreEmail.getEditText().getText().toString().trim();
                String userID = FirebaseAuth.getInstance().getUid();

                Users addUserData = new Users(storeName, storeEmail, phoneNo, storeType, userID);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users").document(userID)
                        .set(addUserData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UsersRegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UsersRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(UsersRegisterActivity.this);
                sharedPreferencesManager.createCurrentUserDetailSharedPreference(storeName, storeEmail, phoneNo, storeType, userID);
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                finish();
            }
        });
    }

    private boolean validateStoreType() {
        if (rgStoreType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select any", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

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
            etStoreEmail.setError("Please enter valid email");
            return false;
        } else {
            etStoreEmail.setError(null);
            return true;
        }
    }
}