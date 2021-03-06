package com.example.deliveryguy.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesManager {
    private SharedPreferences currentUserDetail;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesManager() {
    }

    private static final String IS_LOGIN_USER = "IsLoggedAsUser";
    private static final String IS_LOGIN_DELIVERY_PERSON = "IsLoggedAsDeliveryPerson";

    public static final String KEY_STORE_NAME = "storeName";
    public static final String KEY_STORE_EMAIL = "storeEmail";
    public static final String KEY_STORE_PHONE_NO = "storePhoneNo";
    public static final String KEY_STORE_TYPE = "storeType";
    public static final String KEY_USER_ID = "userID";

    public static final String KEY_DELIVERY_PERSON_NAME = "deliveryPersonName";
    public static final String KEY_DELIVERY_PERSON_EMAIL = "deliveryPersonEmail";
    public static final String KEY_DELIVERY_PERSON_PHONE_NO = "deliveryPersonPhoneNo";
    public static final String KEY_DELIVERY_PERSON_DOB = "deliveryPersonDOB";
    public static final String KEY_DELIVERY_PERSON_GENDER = "deliveryPersonGender";
    public static final String KEY_DELIVERY_PERSON_ID = "deliveryPersonID";

    public SharedPreferencesManager(Context context) {
        this.context = context;
        currentUserDetail = this.context.getSharedPreferences("currentUserDetail", Context.MODE_PRIVATE);
        editor = currentUserDetail.edit();
    }

    public void createCurrentUserDetailSharedPreference(String storeName, String storeEmail, String storePhoneNo, String storeType, String userID) {
        editor.putBoolean(IS_LOGIN_USER, true);
        editor.putString(KEY_STORE_NAME, storeName);
        editor.putString(KEY_STORE_EMAIL, storeEmail);
        editor.putString(KEY_STORE_PHONE_NO, storePhoneNo);
        editor.putString(KEY_STORE_TYPE, storeType);
        editor.putString(KEY_USER_ID, userID);
        editor.commit();
    }

    public HashMap<String, String> getCurrentUserDetailFromSharedPreference() {
        HashMap<String, String> hashMapCurrentUserData = new HashMap<String, String>();
        hashMapCurrentUserData.put(KEY_STORE_NAME, currentUserDetail.getString(KEY_STORE_NAME, null));
        hashMapCurrentUserData.put(KEY_STORE_EMAIL, currentUserDetail.getString(KEY_STORE_EMAIL, null));
        hashMapCurrentUserData.put(KEY_STORE_PHONE_NO, currentUserDetail.getString(KEY_STORE_PHONE_NO, null));
        hashMapCurrentUserData.put(KEY_STORE_TYPE, currentUserDetail.getString(KEY_STORE_TYPE, null));
        hashMapCurrentUserData.put(KEY_USER_ID, currentUserDetail.getString(KEY_USER_ID, null));
        return hashMapCurrentUserData;
    }

    public void createCurrentDeliveryPersonDetailSharedPreferences(String deliveryPersonName, String deliveryPersonEmail, String deliveryPersonPhoneNo, String deliveryPersonDOB, String deliveryPersonGender, String deliveryPersonID) {
        editor.putBoolean(IS_LOGIN_DELIVERY_PERSON, true);
        editor.putString(KEY_DELIVERY_PERSON_NAME, deliveryPersonName);
        editor.putString(KEY_DELIVERY_PERSON_EMAIL, deliveryPersonEmail);
        editor.putString(KEY_DELIVERY_PERSON_PHONE_NO, deliveryPersonPhoneNo);
        editor.putString(KEY_DELIVERY_PERSON_DOB, deliveryPersonDOB);
        editor.putString(KEY_DELIVERY_PERSON_GENDER, deliveryPersonGender);
        editor.putString(KEY_DELIVERY_PERSON_ID, deliveryPersonID);
        editor.commit();
    }

    public HashMap<String, String> getCurrentDeliveryPersonDetailFromSharedPreference() {
        HashMap<String, String> hashMapCurrentUserData = new HashMap<String, String>();
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_NAME, currentUserDetail.getString(KEY_DELIVERY_PERSON_NAME, null));
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_EMAIL, currentUserDetail.getString(KEY_DELIVERY_PERSON_EMAIL, null));
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_PHONE_NO, currentUserDetail.getString(KEY_DELIVERY_PERSON_PHONE_NO, null));
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_DOB, currentUserDetail.getString(KEY_DELIVERY_PERSON_DOB, null));
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_GENDER, currentUserDetail.getString(KEY_DELIVERY_PERSON_GENDER, null));
        hashMapCurrentUserData.put(KEY_DELIVERY_PERSON_ID, currentUserDetail.getString(KEY_DELIVERY_PERSON_ID, null));
        return hashMapCurrentUserData;
    }
}
