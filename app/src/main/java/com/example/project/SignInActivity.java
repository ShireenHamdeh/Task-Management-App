package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private DataBaseHelper dpHelper;
    private EditText EMAIL;
    private EditText PASSWORD;
    private CheckBox CheckBox;
    private SharedPreference sharedPreference;
    TextView signUpLink;
    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EMAIL = findViewById(R.id.emailField);
        PASSWORD = findViewById(R.id.passwordField);
        CheckBox = findViewById(R.id.rememberMeCheckBox);

        sharedPreference = new SharedPreference(this);
        dpHelper = new DataBaseHelper(this);

        String savedEmail = sharedPreference.getEmail();

        if (!TextUtils.isEmpty(savedEmail)) {
            EMAIL.setText(savedEmail);
            CheckBox.setChecked(true);
        }

        signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String email = EMAIL.getText().toString().trim();
                String password = PASSWORD.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "Please fill empty fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignInActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (dpHelper.validateUser(email, password)) {
                    if (CheckBox.isChecked()) {
                        sharedPreference.saveEmail(email);
                    } else {
                        sharedPreference.clearEmail();
                    }
                    Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    intent.putExtra("userEmail", email);

                    intent.putExtra("userName", dpHelper.getUserName(email));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
