// ActivityRegister.java
package com.example.myapplication;

// ActivityRegister.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityRegister extends AppCompatActivity {
    EditText etUsername, etPassword, etConfirmPassword;
    Button btnRegister, btnGoToLogin;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_border);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etCFPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.bthLogin);
        db = new DB(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ActivityRegister.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(ActivityRegister.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isRegistered = db.insertData(username, password);
                    if (isRegistered) {
                        Toast.makeText(ActivityRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActivityRegister.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                startActivity(intent);
            }
        });
    }
}
