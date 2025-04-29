// SharedPreferenceManager.java
package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

    private static final String PREF_NAME = "TASKAPP";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public void clearEmail() {
        editor.remove(KEY_EMAIL);
        editor.commit();
    }

    public boolean writeDarkMode(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean readDarkMode(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
