package com.mehrab.bittrader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrab.bittrader.User.UserInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TradeActivity extends AppCompatActivity {
    private static final String TAG = "TradeActivity";
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private static final DecimalFormat BTC_DF = new DecimalFormat("0.0000");
    private static final String CURRENT_PRICE_URL =
            "https://api.coindesk.com/v1/bpi/currentprice.json";

    private RequestQueue queue;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser_;
    private UserInformation userInformation_;

    private String currentPrice_ = "";
    private double currentPriceDouble_ = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        currentUser_ = mAuth.getCurrentUser();
        if (currentUser_ == null) {
            toLogin();
        }
        updatePrice();
    }

    // fetches current market price
    private void updatePrice() {
        // Make API call
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                CURRENT_PRICE_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Extract data on response and update price
                        try {
                            JSONObject Bpi = response.getJSONObject("bpi");
                            JSONObject Usd = Bpi.getJSONObject("USD");
                            currentPrice_ = "$" + Usd.getString("rate");
                            currentPriceDouble_ = Usd.getDouble("rate_float");
                            // Update price
                            TextView btc_price = (TextView) findViewById(R.id.btc_price);
                            btc_price.setText("$" + DF.format(currentPriceDouble_));

                            getUserData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Cannot fetch data from api");
                    }
                }
        );
        queue.add(request);
    }

    // Fetches user data once on every update
    private void getUserData() {
        // Update textviews
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Save data to userInformation
                DataSnapshot data = dataSnapshot.child(currentUser_.getUid());
                userInformation_ = data.getValue(UserInformation.class);
                updateApp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read user data");
                Toast.makeText(
                        getApplicationContext(),
                        "Password cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Updates app with userInformation_
    private void updateApp() {
        // Grab textviews
        TextView accountValue = (TextView) findViewById(R.id.account_value);
        TextView btcBalance = (TextView) findViewById(R.id.btc_balance);
        TextView usdBalance = (TextView) findViewById(R.id.usd_balance);
        TextView maxValueReached = (TextView) findViewById(R.id.max_value_reached);

        btcBalance.setText(BTC_DF.format(userInformation_.btcBalance) + "");
        usdBalance.setText("$" + DF.format(userInformation_.usdBalance));

        // Calculate account value
        double value = userInformation_.usdBalance + (userInformation_.btcBalance * currentPriceDouble_);
        accountValue.setText("$" + DF.format(value));

        // Set account max value reached
        if (value > userInformation_.maxValueReached) {
            maxValueReached.setText("$" + DF.format(value));

            // Save new max value to account
            userInformation_.maxValueReached = value;
            mDatabase.child(currentUser_.getUid()).setValue(userInformation_);
        }
    }

    // Sell btc
    public void sell(View view) {
        // Grab amount being sold
        EditText btcAmount = (EditText) findViewById(R.id.btc_amount);

        // Check input
        if (btcAmount.getText().toString().matches("")) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter amount to sell",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Double amount = Double.valueOf(btcAmount.getText().toString());
        if (amount > userInformation_.btcBalance) {
            Toast.makeText(
                    getApplicationContext(),
                    "Amount exceeds current BTC Balance",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert amount sold into USD
        Double usd_amount = currentPriceDouble_ * amount;

        // Add usd amount and subtract btc sold from account
        userInformation_.btcBalance -= amount;
        userInformation_.usdBalance += usd_amount;

        // Store user to database
        mDatabase.child(currentUser_.getUid()).setValue(userInformation_);
    }

    // Buy btc
    public void buy(View view) {
        // Grab amount being bought
        EditText btcAmount = (EditText) findViewById(R.id.btc_amount);

        // Check input
        if (btcAmount.getText().toString().matches("")) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter amount to buy",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Double amount = Double.valueOf(btcAmount.getText().toString());
        Double amountUSD = amount * currentPriceDouble_;
        if (amountUSD > userInformation_.usdBalance) {
            Toast.makeText(
                    getApplicationContext(),
                    "Not enough USD",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Subtract usd amount and add btc bought to account
        userInformation_.usdBalance -= amountUSD;
        userInformation_.btcBalance += amount;
        mDatabase.child(currentUser_.getUid()).setValue(userInformation_);
    }

    private void toLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
