package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.authentication.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword, mConfirmPass;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mConfirmPass = findViewById(R.id.confirmPass);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void registerUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String fullName = mFullName.getText().toString();
        String confirmPass = mConfirmPass.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            mFullName.setError("Full name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            mPassword.setError("Password must be >=6 characters");
            return;
        }
        if (confirmPass.isEmpty() || !password.equals(confirmPass)) {
            mConfirmPass.setError("Invalid Password");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                    sendEmailVerification();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegisterActivity.this, "Verification Email has been Sent", Toast.LENGTH_SHORT).show();
                FirebaseUser fUser = fAuth.getCurrentUser();
                String userId = fUser.getUid();
                createUserData(userId, mFullName.getText().toString(), mEmail.getText().toString());
                startActivitysecond();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
            }
        });
    }

    private void createUserData(String userId, String fullName, String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(userId, fullName, email);
        databaseReference.child(userId).setValue(user);
    }

    private void startActivitysecond() {
        // Implement your logic to start the second activity here
    }
}