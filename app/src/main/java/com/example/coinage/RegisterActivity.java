package com.example.coinage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";
    private Button btnFinishRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnFinishRegister = findViewById(R.id.btnFinishRegister);
        btnFinishRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHomeActivity();
            }
        });
    }

    private void goHomeActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}