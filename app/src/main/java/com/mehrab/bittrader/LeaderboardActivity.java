package com.mehrab.bittrader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrab.bittrader.Layout.LeaderboardRecyclerAdapter;
import com.mehrab.bittrader.User.Transaction;
import com.mehrab.bittrader.User.UserInformation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
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
        updateUserList();
    }

    // Shows currently logged in user
    private void updateFooter() {
        TextView footerEmail = (TextView) findViewById(R.id.footer_email);
        footerEmail.setText("Logout: " + currentUser_.getEmail());
    }

    // Fetches all user profiles
    private void updateUserList() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users_ = new ArrayList<UserInformation>();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Double btcBalance = ds.child("btcBalance_").getValue(Double.class);
                    Double usdBalance = ds.child("usdBalance_").getValue(Double.class);
                    Double accountValue = (btcBalance * HomeActivity.currentPriceDouble_) + usdBalance;
                    users_.add(new UserInformation(
                            ds.child("username_").getValue(String.class),
                            btcBalance,
                            usdBalance,
                            accountValue,
                            ds.child("maxValueReached_").getValue(Double.class),
                            new ArrayList<Transaction>()
                    ));
                }
                updateLeaderboard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void updateLeaderboard() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LeaderboardRecyclerAdapter recyclerAdapter = new LeaderboardRecyclerAdapter(users_);
        recyclerView.setAdapter(recyclerAdapter);
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
