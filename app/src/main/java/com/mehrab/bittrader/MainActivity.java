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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehrab.bittrader.User.Transaction;
import com.mehrab.bittrader.User.UserInformation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final double STARTING_BTC_BALANCE = 1.00;
    private static final double STARTING_USD_BALANCE = 10000.00;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is already logged in
        if (currentUser != null) {
            startHomeActivity();
        }
    }


    public void login(View view) {
        // Grab input fields
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        // Check user input
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

        // Login with the provided credentials
        mAuth.signInWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Login successful");
                    currentUser = mAuth.getCurrentUser();
                    startHomeActivity();
                } else {
                    Log.d(TAG, "Login unsuccessful");
                    Toast.makeText(
                            MainActivity.this,
                            task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signup(View view) {
        // Grab input fields
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        // Check user input
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

        // Signup as a new user with the provided credentials
        mAuth.createUserWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Signup Successful");
                    currentUser = mAuth.getCurrentUser();
                    initializeNewUser();
                    startHomeActivity();
                } else {
                    Log.d(TAG, "Signup unsuccessful");
                    Toast.makeText(
                            MainActivity.this,
                            task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Set starting balance for the new user
    private void initializeNewUser() {
        // Extract username from email
        String email = currentUser.getEmail();
        String username = "";

        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                break;
            } else {
                username += email.charAt(i);
            }
        }

        UserInformation newUser = new UserInformation(
                username,
                STARTING_BTC_BALANCE,
                STARTING_USD_BALANCE,
                0,
                new ArrayList<Transaction>());

        // Save user info to database
        mDatabase.child(currentUser.getUid()).setValue(newUser);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
