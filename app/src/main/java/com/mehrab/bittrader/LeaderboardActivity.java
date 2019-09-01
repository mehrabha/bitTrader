package com.mehrab.bittrader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehrab.bittrader.User.UserInformation;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser_;
    ArrayList<UserInformation> users_;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        currentUser_ = mAuth.getCurrentUser();
        if (currentUser_ == null) {
            toLogin();
        }
        updateFooter();
        updateLeaderboard();
    }

    private void updateFooter() {
        TextView footerEmail = (TextView) findViewById(R.id.footer_email);
        footerEmail.setText("Logout: " + currentUser_.getEmail());
    }

    private void updateLeaderboard() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void logout(View view) {
        mAuth.signOut();
        toLogin();
    }

    private void toLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
