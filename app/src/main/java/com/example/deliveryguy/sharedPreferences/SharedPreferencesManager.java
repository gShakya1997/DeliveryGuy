package com.example.deliveryguy.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesManager {
    SharedPreferences currentUserDetail;
    SharedPreferences.Editor editor;
    Context context;

    public SharedPreferencesManager() {
    }

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_STORE_NAME = "storeName";
    public static final String KEY_STORE_EMAIL = "storeEmail";
    public static final String KEY_STORE_PHONE_NO = "storePhoneNo";
    public static final String KEY_STORE_TYPE = "storeType";

    public SharedPreferencesManager(Context context) {
        this.context = context;
        currentUserDetail = this.context.getSharedPreferences("currentUserDetail", Context.MODE_PRIVATE);
        editor = currentUserDetail.edit();
    }

    public void createCurrentUserDetailSharedPreference(String storeName, String storeEmail, String storePhoneNo, String storeType) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_STORE_NAME, storeName);
        editor.putString(KEY_STORE_EMAIL, storeEmail);
        editor.putString(KEY_STORE_PHONE_NO, storePhoneNo);
        editor.putString(KEY_STORE_TYPE, storeType);
        editor.commit();
    }

    public HashMap<String, String> getCurrentUserDetailFromSharedPreference() {
        HashMap<String, String> hashMapCurrentUserData = new HashMap<String, String>();
        hashMapCurrentUserData.put(KEY_STORE_NAME, currentUserDetail.getString(KEY_STORE_NAME, null));
        hashMapCurrentUserData.put(KEY_STORE_EMAIL, currentUserDetail.getString(KEY_STORE_EMAIL, null));
        hashMapCurrentUserData.put(KEY_STORE_PHONE_NO, currentUserDetail.getString(KEY_STORE_PHONE_NO, null));
        hashMapCurrentUserData.put(KEY_STORE_TYPE, currentUserDetail.getString(KEY_STORE_TYPE, null));

        return hashMapCurrentUserData;
    }

    public boolean checkLogin() {
        if (currentUserDetail.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void signOut() {
        editor.clear().commit();
    }
}
