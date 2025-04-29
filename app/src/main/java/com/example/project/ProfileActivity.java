package com.example.project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextView usernameText;
    private Button saveButton;
    private DataBaseHelper dbHelper;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize all components
        setContentView(R.layout.activity_profile);
        usernameText = findViewById(R.id.UserName);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        saveButton = findViewById(R.id.save_button);
        dbHelper = new DataBaseHelper(this);
        //load the email of the user to put it by default in the emailtextview
        String email = getIntent().getStringExtra("email");
        // Load current email, first name, and last name
        emailEditText.setText(email);
        usernameText.setText(dbHelper.getUserName(email));
        //we used that to put the new email in it
        sharedPreference = new SharedPreference(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when i choose save button , extart all the new data
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //check if the new email is valid
                if (!isValidEmail(email)) {
                    Toast.makeText(ProfileActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if the email didnot change and its been used before
                if(!getIntent().getStringExtra("email").equals(email)
                        && dbHelper.isEmailUsed(email)){
                    Toast.makeText(ProfileActivity.this, "Email address is used", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TextUtils.isEmpty() is a utility method provided by the Android TextUtils class.
                //It returns true if:
                //The input string is null, or The input string has a length of 0.

                //if the password is not vaild and not empty
                if (!TextUtils.isEmpty(password) && !isValidPassword(password)) {
                    Toast.makeText(ProfileActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if it passes all violation  then the change will be saved

                confirmChanges(email, password);
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.length() <= 12 &&
                password.matches(".*[A-Z].*")&&
                password.matches(".*\\d.*") &&
                password.matches(".*[a-z].*");
    }

    private void confirmChanges(String email, String password) {

        boolean isEmailChanged = false, isPasswordChanged = false;
        String oldEmail = getIntent().getStringExtra("email");

        //if the email is changed
        if(!oldEmail.equals(email)){
            isEmailChanged = true;
        }
        //if the password changed
        if(password != null && !password.isEmpty()){
            isPasswordChanged = true;
        }

        boolean finalIsEmailChanged = isEmailChanged;
        boolean finalIsPasswordChanged = isPasswordChanged;

        // this is to uniform the user , that the changes will happen
        //it will show screen on the screen , and this is its details
        new AlertDialog.Builder(this)
                .setTitle("Confirm Changes")
                .setMessage("Are you sure you want to save these changes?")
                //if the user click on yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //save the email if it changed
                        if(finalIsEmailChanged){
                            saveNewEmail(email);
                        }
                        //save the password if it changed
                        if (finalIsPasswordChanged){
                            saveNewPassword(password);
                        }
                    }
                })
                //if the user clicked no ,then do nothing
                .setNegativeButton("No", null)
                .show();
    }

    private void saveNewEmail(String email) {
        //save the email in shared preferences, so when we use it later  we call the shared preferences
        sharedPreference.saveEmail(email); // Update email in SharedPreferences
        String oldEmail = getIntent().getStringExtra("email");
        //remove the old email , so the program will not be confused
        dbHelper.updateEmail(oldEmail, email);

        Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void saveNewPassword(String password) {
        //update the email in the database
        dbHelper.updatePassword(getIntent().getStringExtra("email"), password);
        Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
    }
}