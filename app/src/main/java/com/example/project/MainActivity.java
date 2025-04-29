package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference = new SharedPreference(this);
        boolean isDarkMode = sharedPreference.readDarkMode("Mode", false);
        applyDarkMode(isDarkMode);
    }

    public void SignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void applyDarkMode(boolean enable) {
        if (enable) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
