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
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.dashboards.*;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class UsersCodeVerificationActivity extends AppCompatActivity {

    private PinView pvVerificationCode;
    private Button btnVerify;
    private TextView tvTitle, tvDesc;
    private ImageView logo;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String verificationCodeBySystem, phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_code_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();
        phoneNo = getIntent().getStringExtra("PhoneNo");
        sendVerificationCode(phoneNo);
    }

    private void sendVerificationCode(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,   // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pvVerificationCode.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(UsersCodeVerificationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    };

    private void verifyCode(String codeByUser) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInWithPhone(credential);
        } catch (Exception e){
            Toast.makeText(this, "Verification code is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(UsersCodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UsersCodeVerificationActivity.this, "Verification complete!", Toast.LENGTH_SHORT).show();
                    checkRegisteredUser();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(UsersCodeVerificationActivity.this, "Verification not completed! try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkRegisteredUser() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String get_firebase_store_name = documentSnapshot.getString("storeName");
                        String get_firebase_store_email = documentSnapshot.getString("storeEmail");
                        String get_firebase_store_phoneNo = documentSnapshot.getString("storePhoneNo");
                        String get_firebase_store_type = documentSnapshot.getString("storeType");
                        String get_firebase_user_ID = documentSnapshot.getString("userID");
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(UsersCodeVerificationActivity.this);
                        sharedPreferencesManager.createCurrentUserDetailSharedPreference(get_firebase_store_name, get_firebase_store_email, get_firebase_store_phoneNo, get_firebase_store_type, get_firebase_user_ID);

                        Intent intent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), UsersRegisterActivity.class);
                        intent.putExtra("AddPhoneNo", phoneNo);
                        Pair[] pairs = new Pair[4];
                        pairs[0] = new Pair<View, String>(logo, "logoImg");
                        pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
                        pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
                        pairs[3] = new Pair<View, String>(btnVerify, "pageButton");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(UsersCodeVerificationActivity.this, pairs);
                            startActivity(intent, activityOptions.toBundle());
                        }
                    }
                } else {
                    Toast.makeText(UsersCodeVerificationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initialize() {
        pvVerificationCode = findViewById(R.id.pvVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
    }

    public void callNextScreenFromOTP(View view) {
        String code = pvVerificationCode.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }
}