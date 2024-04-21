package com.example.coursetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    // Declare the edit texts
    EditText username_input;
    EditText password_input;
    EditText phone_input;
    // Declare the buttons
    Button login_button;
    Button register_button;
    DBHelper myDB;
    // Declare variables
    CheckBox remember_me_box;
    String enteredPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI edit text fields
        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        phone_input = findViewById(R.id.phone_input);
        // Initialize UI buttons
        login_button = findViewById(R.id.login_button);
        register_button = findViewById(R.id.register_button);
        remember_me_box = findViewById(R.id.remember_me_box);

        // autofill if "Remember Me" is checked
        autofillCredentials();

        myDB = new DBHelper(LoginActivity.this);
        User user = User.getInstance();

        // Register Button Click Listener
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the credentials from the ui
                String enteredUsername = username_input.getText().toString().trim();
                String enteredPassword = password_input.getText().toString().trim();

                addCredentials(enteredUsername);
                // check if a phone is entered, add it if it is valid
                if (isValidCredentials(enteredUsername, enteredPassword)) {
                    // Save credentials if "Remember Me" checkbox is checked
                    saveCredentials(enteredUsername, enteredPassword, enteredPhone, remember_me_box.isChecked());
                    user.setUsername(enteredUsername); // save the username to the singleton instance
                    myDB.addUser(enteredUsername, enteredPassword); // add the user and password to the db
                    myDB.close();
                    Toast.makeText(LoginActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login Button Click Listener
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the credentials from the ui
                String enteredUsername = username_input.getText().toString().trim();
                String enteredPassword = password_input.getText().toString().trim();

                // if the credentials meet the requirements
                if (isValidCredentials(enteredUsername, enteredPassword)) {
                    // Save credentials if "Remember Me" is checked
                    saveCredentials(enteredUsername, enteredPassword, enteredPhone, remember_me_box.isChecked());
                    addCredentials(enteredUsername);
                    // if the user's credentials are correct, the user is logged in.
                    boolean isLoggedIn = myDB.checkUser(enteredUsername, enteredPassword);
                    myDB.close();
                    if (isLoggedIn) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // once logged in, start the main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed, please check your credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void addCredentials(String enteredUsername) {
        User user = User.getInstance(); // get instance of user singleton
        // check if a phone is entered, add it if it is valid
        String enteredPhoneText = phone_input != null ? phone_input.getText().toString().trim() : null;
        if (enteredPhoneText != null && !enteredPhoneText.isEmpty()) {
            if (isValidPhone(enteredPhoneText)) {  // check if the field is valid
                Toast.makeText(LoginActivity.this, "User phone added successfully.", Toast.LENGTH_SHORT).show();
                user.setPhone(enteredPhoneText);
            } else {
                Toast.makeText(LoginActivity.this, "Phone number is invalid", Toast.LENGTH_SHORT).show();
            }
        }
        user.setUsername(enteredUsername);  // save the username to the singleton instance
    }


    // save the credentials in shared preferences
    private void saveCredentials(String username, String password, String phone, boolean rememberMe) {
        SharedPreferences preferences = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // save the credentials to the shared preferences
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("phone", phone);

        // save the remember me preference
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }

    // Retrieve and autofill credentials if "Remember Me" button was checked
    private void autofillCredentials() {
        SharedPreferences preferences = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
        // default is do not remember the user
        boolean rememberMe = preferences.getBoolean("rememberMe", false);
        // if the preference is to remember the user, get the credentials
        if (rememberMe) {
            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");
            String phone = preferences.getString("phone", "");

            // Autofill the login fields
            username_input.setText(username);
            password_input.setText(password);
            phone_input.setText(phone);

            // save the remember be preference
            remember_me_box.setChecked(true);
        }
    }

    // Validate the entered credentials
    private boolean isValidCredentials(String username, String password) {
        if (username.length() >= 6 && password.length() >= 6) {
            return true;
        } else {
            Toast.makeText(LoginActivity.this, "Username and password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    // Validate the entered phone
    private boolean isValidPhone(String phone) {
        if ((phone.length() == 11 && phone.startsWith("1")) || (phone.isEmpty())){
            return true;
        } else {
            Toast.makeText(LoginActivity.this, "Phone number must be 11 digits and start with 1", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
