package com.example.deliveryguy.activities.registerAndLogin;

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
import com.example.deliveryguy.activities.DashboardActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class CodeVerificationActivity extends AppCompatActivity {
    private PinView pvVerificationCode;
    private Button btnVerify;
    private TextView tvTitle, tvDesc;
    private ImageView logo;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String verificationCodeBySystem, phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        initialize();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            Toast.makeText(CodeVerificationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInWithPhone(credential);
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(CodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CodeVerificationActivity.this, "Verification complete!", Toast.LENGTH_SHORT).show();
                    checkUserByPhoneNumber();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(CodeVerificationActivity.this, "Verification not completed! try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkUserByPhoneNumber() {
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("storePhoneNo").equalTo(phoneNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String get_firebase_store_name = snapshot.child(phoneNo).child("storeName").getValue(String.class);
                    String get_firebase_store_email = snapshot.child(phoneNo).child("storeEmail").getValue(String.class);
                    String get_firebase_store_phoneNo = snapshot.child(phoneNo).child("storePhoneNo").getValue(String.class);
                    String get_firebase_store_type = snapshot.child(phoneNo).child("storeType").getValue(String.class);

                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(CodeVerificationActivity.this);
                    sharedPreferencesManager.createCurrentUserDetailSharedPreference(get_firebase_store_name, get_firebase_store_email, get_firebase_store_phoneNo, get_firebase_store_type);

                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), CustomerRegisterActivity.class);
                    intent.putExtra("AddPhoneNo", phoneNo);
                    Pair[] pairs = new Pair[4];
                    pairs[0] = new Pair<View, String>(logo, "logoImg");
                    pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
                    pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
                    pairs[3] = new Pair<View, String>(btnVerify, "pageButton");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(CodeVerificationActivity.this, pairs);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CodeVerificationActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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