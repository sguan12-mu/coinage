package com.example.coinage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// register new user with name, email, password
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";

    private TextInputEditText tiName;
    private TextInputEditText tiEmailReg;
    private TextInputEditText tiPasswordReg;
    private TextInputEditText tiPasswordConfirm;
    private Button btnFinishRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tiName = findViewById(R.id.tiName);
        tiEmailReg = findViewById(R.id.tiEmailReg);
        tiPasswordReg = findViewById(R.id.tiPasswordReg);
        tiPasswordConfirm = findViewById(R.id.tiPasswordConfirm);
        btnFinishRegister = findViewById(R.id.btnFinishRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnFinishRegister.setOnClickListener((View v) -> {
                String name = tiName.getText().toString();
                String email = tiEmailReg.getText().toString();
                String password = tiPasswordReg.getText().toString();
                String passwordConfirm = tiPasswordConfirm.getText().toString();
                if (password.equals(passwordConfirm)) {
                    signupUser(name, email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "passwords don't match", Toast.LENGTH_SHORT).show();
                }
            });

        tvLogin.setOnClickListener((View v) -> {
            goLoginActivity();
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "proceed to app (default page is transaction list)");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goLoginActivity() {
        Log.i(TAG, "go to login user activity");
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void signupUser(String name, String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    Log.e(TAG, "issue with register", e);
                    Toast.makeText(RegisterActivity.this, "issue with register", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}