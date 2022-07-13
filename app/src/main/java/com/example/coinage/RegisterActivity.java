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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// register new user with name, email, password
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";

    private EditText etName;
    private EditText etEmailReg;
    private EditText etPasswordReg;
    private EditText etPasswordConfirm;
    private Button btnFinishRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmailReg = findViewById(R.id.etEmailReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnFinishRegister = findViewById(R.id.btnFinishRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnFinishRegister.setOnClickListener((View v) -> {
                String name = etName.getText().toString();
                String email = etEmailReg.getText().toString();
                String password = etPasswordReg.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
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