package com.civicx.android.utils;

import android.app.Application;

import com.civicx.android.constants.Constants;

public class GetColor extends Application {

    public static String getColor(String house) {

        switch (house) {
            case Constants.NATIONAL:
                return "#996600";
            case Constants.NGO:
                return "#151B54";
            default:
                return "#006400";
        }
    }
}
