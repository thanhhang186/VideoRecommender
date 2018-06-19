package com.khtn.videorecommendation.videorecommendation.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static String PREF_NAME = "prefs";
    public static final String USER_ID = "userId";
    private static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public static void putUserID(Context context, String string){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(USER_ID, string);
        editor.commit();
    }

    public static String getUserId(Context context){
        return getPref(context).getString(USER_ID,"");
    }
}
