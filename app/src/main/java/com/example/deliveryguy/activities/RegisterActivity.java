package com.example.deliveryguy.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.MainActivity;
import com.example.deliveryguy.R;
import com.example.deliveryguy.bll.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();
        actionButtons();
    }

    private void actionButtons() {
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View,String>(logo,"logoImg");
                pairs[1] = new Pair<View,String>(tvTitle,"pageTitle");
                pairs[2] = new Pair<View,String>(tvDesc,"pageDesc");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this,pairs);
                    startActivity(intent, activityOptions.toBundle());
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateStoreName() | !validateStoreEmail() | !validateStorePassword() | !validateStoreConfirmPassword()) {
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(storeEmail, storePassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this,
                                            "User Created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                            "Error " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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