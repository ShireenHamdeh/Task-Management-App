// SignUpActivity.java
package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private EditText EMAIL;
    private EditText FIRSTNAME;
    private EditText LASTNAME;
    private EditText PASSWORD;
    private EditText CONFIRM_PASSWORD;
    private TextView EMAIL_ERROR_MSG;
    private TextView FIRST_NAME_ERROR_MSG;
    private TextView LAST_NAME_ERROR_MSG;
    private TextView PASSWORD_ERROR_MSG;
    private TextView CONFIRM_PASSWORD_ERROR_MSG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EMAIL = findViewById(R.id.emailField);
        FIRSTNAME = findViewById(R.id.firstNameField);
        LASTNAME = findViewById(R.id.lastNameField);
        PASSWORD = findViewById(R.id.passwordField);
        CONFIRM_PASSWORD = findViewById(R.id.confirmPasswordField);

        EMAIL_ERROR_MSG = findViewById(R.id.email_textView);
        FIRST_NAME_ERROR_MSG= findViewById(R.id.firstName_textView);
        LAST_NAME_ERROR_MSG= findViewById(R.id.lastName_textView);
        PASSWORD_ERROR_MSG= findViewById(R.id.password_textView);
        CONFIRM_PASSWORD_ERROR_MSG= findViewById(R.id.confirmPassword_textView);

        dataBaseHelper = new DataBaseHelper(this);
    }

    @SuppressLint("SetTextI18n")
    public void register(View view) {
        resetFieldColors();
        String email = EMAIL.getText().toString().trim();
        String firstName = FIRSTNAME.getText().toString().trim();
        String lastName = LASTNAME.getText().toString().trim();
        String password = PASSWORD.getText().toString().trim();
        String confirmPassword = CONFIRM_PASSWORD.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(confirmPassword) && TextUtils.isEmpty(password)) {
            markRed();
            Toast.makeText(this, "Please Empty fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EMAIL.setHintTextColor(Color.RED);
            EMAIL.setTextColor(Color.RED);
            EMAIL_ERROR_MSG.setText("Please enter email with correct format");
            isValid = false;

        }  else if (dataBaseHelper.isEmailUsed(email)) {
            EMAIL.setHintTextColor(Color.RED);
            EMAIL.setTextColor(Color.RED);
            EMAIL_ERROR_MSG.setText("Email is already registered.");
            Toast.makeText(this, "Email is already registered.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (TextUtils.isEmpty(firstName) || firstName.length() < 5 || firstName.length() > 20) {
            FIRSTNAME.setHintTextColor(Color.RED);
            FIRSTNAME.setTextColor(Color.RED);
            FIRST_NAME_ERROR_MSG.setText("First name must be between 5 and 20 characters.");
            isValid = false;
        }

        if (TextUtils.isEmpty(lastName) || lastName.length() < 5 || lastName.length() > 20) {
            LASTNAME.setHintTextColor(Color.RED);
            LASTNAME.setTextColor(Color.RED);
            LAST_NAME_ERROR_MSG.setText("Last name must be between 5 and 20 characters.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password) || !isValidPassword(password)) {
            PASSWORD.setHintTextColor(Color.RED);
            PASSWORD.setTextColor(Color.RED);
            PASSWORD_ERROR_MSG.setText("Please use strong Password with 6-12 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            CONFIRM_PASSWORD.setHintTextColor(Color.RED);
            CONFIRM_PASSWORD.setTextColor(Color.RED);
            CONFIRM_PASSWORD_ERROR_MSG.setText("Passwords don't match.");
            isValid = false;
        }

        if(isValid){
            if (dataBaseHelper.addUser(email, firstName, lastName, password)) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Email already exist.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.length() <= 12 &&
                password.matches(".*[A-Z].*")&&
                password.matches(".*\\d.*") &&
                password.matches(".*[a-z].*");
    }

    private void markRed() {
        EMAIL.setHintTextColor(Color.RED);
        FIRSTNAME.setHintTextColor(Color.RED);
        LASTNAME.setHintTextColor(Color.RED);
        PASSWORD.setHintTextColor(Color.RED);
        CONFIRM_PASSWORD.setHintTextColor(Color.RED);
    }

    private void resetFieldColors() {
        EMAIL.setTextColor(Color.BLACK);
        EMAIL.setHintTextColor(Color.GRAY);
        FIRSTNAME.setTextColor(Color.BLACK);
        FIRSTNAME.setHintTextColor(Color.GRAY);
        LASTNAME.setTextColor(Color.BLACK);
        LASTNAME.setHintTextColor(Color.GRAY);
        PASSWORD.setTextColor(Color.BLACK);
        PASSWORD.setHintTextColor(Color.GRAY);
        CONFIRM_PASSWORD.setTextColor(Color.BLACK);
        CONFIRM_PASSWORD.setHintTextColor(Color.GRAY);
        EMAIL_ERROR_MSG.setText("");
        FIRST_NAME_ERROR_MSG.setText("");
        LAST_NAME_ERROR_MSG.setText("");
        PASSWORD_ERROR_MSG.setText("");
        CONFIRM_PASSWORD_ERROR_MSG.setText("");
    }
}
