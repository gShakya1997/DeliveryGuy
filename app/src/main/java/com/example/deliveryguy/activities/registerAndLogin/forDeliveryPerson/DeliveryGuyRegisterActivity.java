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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryguy.R;
import com.example.deliveryguy.activities.DashboardActivity;
import com.example.deliveryguy.activities.registerAndLogin.forUser.UsersRegisterActivity;
import com.example.deliveryguy.bll.Validation;
import com.example.deliveryguy.models.DeliveryPerson;
import com.example.deliveryguy.sharedPreferences.SharedPreferencesManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class DeliveryGuyRegisterActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView tvTitle, tvDesc;
    private TextInputLayout etDeliveryPersonName, etDeliveryPersonEmail;
    private Button btnDeliveryGuyRegister;
    private RadioGroup rgGender;
    private DatePicker datePickerAge;
    private Validation validation = new Validation();
    private String deliveryPersonName, deliveryPersonEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_guy_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding();
        actionButtons();
    }

    private void actionButtons() {
        btnDeliveryGuyRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAge() | !validateGender() | !validateDeliveryPersonName() | !validateDeliveryPersonEmail()) {
                    return;
                }
                int selectedGender = rgGender.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedGender);
                int day = datePickerAge.getDayOfMonth();
                int month = datePickerAge.getMonth();
                int year = datePickerAge.getYear();

                String gender = radioButton.getText().toString().trim();
                String dateOfBirth = day + "/" + month + "/" + year;
                String deliveryPersonName = etDeliveryPersonName.getEditText().getText().toString().trim();
                String deliveryPersonEmail = etDeliveryPersonEmail.getEditText().getText().toString().trim();
                String deliveryPersonPhoneNo = getIntent().getStringExtra("AddPhoneNo");

                DeliveryPerson addDeliveryPersonData = new DeliveryPerson(deliveryPersonName, deliveryPersonEmail, deliveryPersonPhoneNo, dateOfBirth, gender);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("delivery_person").document(deliveryPersonPhoneNo)
                        .set(addDeliveryPersonData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DeliveryGuyRegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DeliveryGuyRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(DeliveryGuyRegisterActivity.this);
                sharedPreferencesManager.createCurrentDeliveryPersonDetailSharedPreferences(deliveryPersonName, deliveryPersonEmail, deliveryPersonPhoneNo, dateOfBirth, gender);

                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(logo, "logoImg");
                pairs[1] = new Pair<View, String>(tvTitle, "pageTitle");
                pairs[2] = new Pair<View, String>(tvDesc, "pageDesc");
                pairs[3] = new Pair<View, String>(btnDeliveryGuyRegister, "pageButton");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation
                            (DeliveryGuyRegisterActivity.this, pairs);
                    startActivity(intent, activityOptions.toBundle());
                }
            }
        });
    }

    private void binding() {
        logo = findViewById(R.id.logo);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        rgGender = findViewById(R.id.rgGender);
        etDeliveryPersonName = findViewById(R.id.etDeliveryPersonName);
        etDeliveryPersonEmail = findViewById(R.id.etDeliveryPersonEmail);
        datePickerAge = findViewById(R.id.datePickerAge);
        btnDeliveryGuyRegister = findViewById(R.id.btnDeliveryGuyRegister);
    }

    private boolean validateGender() {
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select any", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int deliveryPersonAge = datePickerAge.getYear();
        int isAgeValid = currentYear - deliveryPersonAge;

        if (isAgeValid < 18) {
            Toast.makeText(this, "You must be 18+ to be delivery person", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateDeliveryPersonName() {
        deliveryPersonName = etDeliveryPersonName.getEditText().getText().toString().trim();
        if (!validation.validateStoreName(deliveryPersonName)) {
            etDeliveryPersonName.setError("Required");
            return false;
        } else {
            etDeliveryPersonName.setError(null);
            return true;
        }
    }

    private boolean validateDeliveryPersonEmail() {
        deliveryPersonEmail = etDeliveryPersonEmail.getEditText().getText().toString().trim();
        if (validation.validateEmail(deliveryPersonEmail).equals("required")) {
            etDeliveryPersonEmail.setError("Required");
            return false;
        } else if (validation.validateEmail(deliveryPersonEmail).equals("invalid")) {
            etDeliveryPersonEmail.setError("Please enter valid email");
            return false;
        } else {
            etDeliveryPersonEmail.setError(null);
            return true;
        }
    }
}