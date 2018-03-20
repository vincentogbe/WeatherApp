package com.example.vincentogbe.zone;

import android.app.Activity;
import android.content.SharedPreferences;
/**
 * Created by vincent ogbe on 07/11/2017.
 */

public class city_zone {
    SharedPreferences prefs;

    public city_zone(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Oulu, FI");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
