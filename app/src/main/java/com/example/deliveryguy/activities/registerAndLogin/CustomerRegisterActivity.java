package com.example.deliveryguy.activities.registerAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.DashboardActivity;
import com.example.deliveryguy.activities.SplashScreenActivity;
import com.example.deliveryguy.bll.Validation;
import com.example.deliveryguy.models.Users;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegisterActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_customer_register);
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

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                Users addUserData = new Users(storeName, storeEmail, phoneNo, storeType);
                databaseReference.child(phoneNo).setValue(addUserData);


                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                //Animation
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(logo, "logoImg");
                pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
                pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
                pairs[3] = new Pair<View, String>(btnRegister, "pageButton");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation
                            (CustomerRegisterActivity.this, pairs);
                    startActivity(intent, activityOptions.toBundle());
                }
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