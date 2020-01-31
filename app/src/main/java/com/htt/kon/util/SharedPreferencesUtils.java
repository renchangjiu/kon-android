package com.htt.kon.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences utils
 *
 * @author su
 * @date 2020/01/23 20:22
 */
public class SharedPreferencesUtils {

    /**
     * SharedPreferences file key
     */
    private static final String PREFERENCE_FILE_KEY = "com.su.news";

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

}
