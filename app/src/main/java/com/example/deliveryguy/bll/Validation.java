package com.example.deliveryguy.bll;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validation {
    private String throwError;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=<>_!*().,])" +    //at least 1 special character
                    ".{6,}" +               //at least 6 characters
                    "$");

    public boolean validateStoreName(String storeName){
        return !storeName.isEmpty();
    }

    public String validatePhone(String regPhone) {
        if (regPhone.isEmpty()) {
            throwError = "required";
            return throwError;
        } else if (regPhone.length() < 10) {
            throwError = "invalidPhone";
            return throwError;
        } else {
            throwError = "noError";
            return throwError;
        }
    }

    public String validateEmail(String storeEmail) {
        if (storeEmail.isEmpty()) {
            throwError = "required";
            return throwError;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(storeEmail).matches()) {
            throwError = "invalid";
            return throwError;
        } else {
            throwError = "noError";
            return throwError;
        }
    }

    public String validatePassword(String storePassword) {
        if (storePassword.isEmpty()) {
            throwError = "required";
            return throwError;
        } else if (!PASSWORD_PATTERN.matcher(storePassword).matches()) {
            throwError = "weak";
            return throwError;
        } else {
            throwError = "noError";
            return throwError;
        }
    }

    public String validateConfirmPassword(String storePassword, String storeCPassword) {
        if (!storePassword.equals(storeCPassword)) {
            throwError = "!Password";
            return throwError;
        } else if (storeCPassword.isEmpty()) {
            throwError = "required";
            return throwError;
        } else {
            throwError = "noError";
            return throwError;
        }
    }
}
