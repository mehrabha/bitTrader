package com.mehrab.bittrader;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startHome();
        }
    }
    public void login(View view) {
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);
    }

    public void signup(View view) {
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        if (email.getText().toString().matches("")) {
            Toast.makeText(
                    this,
                    "Email cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (password.getText().toString().matches("")) {
            Toast.makeText(
                    this,
                    "Password cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Signup new user
        mAuth.createUserWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Sign Up Successful");
                    startHome();
                } else {
                    Log.d(TAG, "Sign Up unsuccessful");
                    Toast.makeText(
                            MainActivity.this,
                            "Unable to create account",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
