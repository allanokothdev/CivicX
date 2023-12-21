package com.civicx.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.civicx.android.constants.Constants;
import com.civicx.android.models.User;

public class GetUser extends Application {

    public static User getUser(Context context, String currentUserID) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_STORE_REFERENCE, Context.MODE_PRIVATE);
        return new User(currentUserID, prefs.getString(Constants.PIC, Constants.PIC), prefs.getString(Constants.NAME, Constants.NAME), prefs.getString(Constants.EMAIL, Constants.EMAIL),prefs.getString(Constants.AGE, Constants.AGE),prefs.getBoolean(Constants.DISABILITY,false),prefs.getString(Constants.WARD, Constants.WARD),prefs.getString(Constants.SUB_COUNTY, Constants.SUB_COUNTY), prefs.getString(Constants.CONSTITUENCY, Constants.CONSTITUENCY), prefs.getString(Constants.COUNTY, Constants.COUNTY),prefs.getString(Constants.GENDER, Constants.GENDER),prefs.getString(Constants.COUNTRY,Constants.COUNTRY), prefs.getString(Constants.TOKEN,Constants.TOKEN),prefs.getInt(Constants.STARS,0),prefs.getBoolean(Constants.VERIFICATION,false)  ,prefs.getBoolean(Constants.REPORTED,false));
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.KEY_STORE_REFERENCE,Context.MODE_PRIVATE).edit();
        editor.putString(Constants.PIC, user.getPic());
        editor.putString(Constants.NAME, user.getName());
        editor.putString(Constants.EMAIL, user.getEmail());
        editor.putString(Constants.COUNTY, user.getCounty());
        editor.putString(Constants.AGE, user.getAge());
        editor.putString(Constants.GENDER, user.getGender());
        editor.putString(Constants.WARD, user.getWard());
        editor.putString(Constants.SUB_COUNTY, user.getSubCounty());
        editor.putString(Constants.CONSTITUENCY, user.getConstituency());
        editor.putString(Constants.COUNTRY, user.getCountry());
        editor.putString(Constants.TOKEN, user.getToken());
        editor.putInt(Constants.STARS, user.getStars());
        editor.putBoolean(Constants.DISABILITY, user.isDisability());
        editor.putBoolean(Constants.VERIFICATION, user.isVerification());
        editor.putBoolean(Constants.REPORTED, user.isReported());
        editor.apply();
    }

    public static void saveObject(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.KEY_STORE_REFERENCE,Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}
