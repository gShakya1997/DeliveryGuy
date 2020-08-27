package com.example.deliveryguy.activities.registerAndLogin.forDeliveryPerson;

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

public class DeliveryGuyCodeVerificationActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_delivery_guy_code_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding();
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
            Toast.makeText(DeliveryGuyCodeVerificationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInWithPhone(credential);
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(DeliveryGuyCodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DeliveryGuyCodeVerificationActivity.this, "Verification complete!", Toast.LENGTH_SHORT).show();
                    checkRegisteredUser();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(DeliveryGuyCodeVerificationActivity.this, "Verification not completed! try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkRegisteredUser() {
        DocumentReference documentReference = firebaseFirestore.collection("delivery_person").document(FirebaseAuth.getInstance().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String get_firebase_delivery_person_name = documentSnapshot.getString("deliveryPersonName");
                        String get_firebase_delivery_person_email = documentSnapshot.getString("deliveryPersonEmail");
                        String get_firebase_delivery_person_phone_no = documentSnapshot.getString("deliveryPersonPhoneNo");
                        String get_firebase_delivery_person_dob = documentSnapshot.getString("deliveryPersonDOB");
                        String get_firebase_delivery_person_gender = documentSnapshot.getString("deliveryPersonGender");
                        String get_firebase_delivery_person_ID = documentSnapshot.getString("deliveryPersonID");
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(DeliveryGuyCodeVerificationActivity.this);
                        sharedPreferencesManager.createCurrentDeliveryPersonDetailSharedPreferences(get_firebase_delivery_person_name, get_firebase_delivery_person_email, get_firebase_delivery_person_phone_no, get_firebase_delivery_person_dob, get_firebase_delivery_person_gender, get_firebase_delivery_person_ID);
                        Intent intent = new Intent(getApplicationContext(), DeliveryPersonDashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DeliveryGuyRegisterActivity.class);
                        intent.putExtra("AddPhoneNo", phoneNo);
                        Pair[] pairs = new Pair[4];
                        pairs[0] = new Pair<View, String>(logo, "logoImg");
                        pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
                        pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
                        pairs[3] = new Pair<View, String>(btnVerify, "pageButton");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(DeliveryGuyCodeVerificationActivity.this, pairs);
                            startActivity(intent, activityOptions.toBundle());
                        }
                    }
                } else {
                    Toast.makeText(DeliveryGuyCodeVerificationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void binding() {
        pvVerificationCode = findViewById(R.id.pvVerificationCode);
        btnVerify = findViewById(R.id.btnVerify);
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
    }

    public void callNextScreenFromOTPDeliveryPerson(View view) {
        String code = pvVerificationCode.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }
}