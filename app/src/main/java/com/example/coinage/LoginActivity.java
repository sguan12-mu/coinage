package com.example.coinage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;

// users log in to their account with username or email and password
public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    private TextInputEditText tiEmail;
    private TextInputEditText tiPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ParseUser.logOut();
        }

        tiEmail = findViewById(R.id.tiEmail);
        tiPassword = findViewById(R.id.tiPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener((View v) -> {
                String email = tiEmail.getText().toString();
                String password = tiPassword.getText().toString();
                loginUser(email, password);
            });

        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener((View v) -> goRegisterActivity());
    }

    private void goMainActivity() {
        Log.i(TAG, "proceed to app (default page is transaction list)");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goRegisterActivity() {
        Log.i(TAG, "go to register user activity");
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    private void loginUser(String email, String password) {
        if (email == null || password == null) {
            Toast.makeText(LoginActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
            }
        });
    }
}