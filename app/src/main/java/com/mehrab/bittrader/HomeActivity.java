package com.mehrab.bittrader;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mehrab.bittrader.User.UserInformation;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity.java";
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private static final DecimalFormat BTC_DF = new DecimalFormat("0.0000");

    private RequestQueue queue;
    private static final String CURRENT_PRICE_URL =
            "https://api.coindesk.com/v1/bpi/currentprice.json";
    private static final String HISTORICAL_PRICE_URL =
            "https://api.coindesk.com/v1/bpi/historical/close.json";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser_;
    private UserInformation userInformation_;

    private List<DataPoint> datapoints_ = new ArrayList<DataPoint>();
    private String currentPrice_ = "";
    private double currentPriceDouble_ = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser_ = mAuth.getCurrentUser();

        // Return to MainActivity if user is not logged in
        if (currentUser_ == null) {
            toLogin();
        }

        updateFooter();
        updatePrice();
        // updateDatapoints(); Gets called from updatePrice()
        //getUserData(); Gets called from updatePrice()
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

    // Shows the logged in user's email
    private void updateFooter() {
        TextView footerEmail = (TextView) findViewById(R.id.footer_email);
        footerEmail.setText("Logout: " + currentUser_.getEmail());
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

                        // FInd the time of update
                        JSONObject Time = response.getJSONObject("time");
                        TextView last_updated = (TextView) findViewById(R.id.last_updated);
                        last_updated.setText("Updated: " + Time.getString("updated"));
                        updateDatapoints();
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

    // Fetches BTC historic prices and stores in datapoints_
    private void updateDatapoints() {
        datapoints_.clear();
        //Make API call
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            HISTORICAL_PRICE_URL,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Extract data from api and update graph
                    try {
                        JSONObject Bpi = response.getJSONObject("bpi");
                        int x_val = 0;
                        Iterator iter = Bpi.keys();

                        while (iter.hasNext()) {
                            String key = iter.next().toString();
                            double value = Bpi.getDouble(key);
                            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                datapoints_.add(new DataPoint(parser.parse(key), value));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        // Add today's price
                        datapoints_.add(new DataPoint(new Date(), currentPriceDouble_));
                        updateGraph();
                        updatePriceInfo();
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

    // Updates graph using the entries in datapoints_
    private void updateGraph() {
        GraphView graph = (GraphView) findViewById(R.id.main_graph);

        int mediumShade = Color.argb(80, 255, 255, 255);
        int highShade = Color.argb(120, 255, 255, 255);

        // Set graph properties
        graph.getGridLabelRenderer().setGridColor(mediumShade);
        graph.getGridLabelRenderer().setVerticalLabelsColor(highShade);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(highShade);
        graph.getGridLabelRenderer().reloadStyles();

        DataPoint[] datapoints = new DataPoint[datapoints_.size()];

        for (int i = 0; i < datapoints_.size(); i++) {
            datapoints[i] = datapoints_.get(i);
        }

        // Create series from datapoints
        LineGraphSeries series = new LineGraphSeries(datapoints);
        series.setColor(highShade);
        series.setThickness(5);

        // Add series to graph
        graph.addSeries(series);

        // Date label formatter for x axis
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setTextSize(24);

        // Set bounds
        graph.getViewport().setXAxisBoundsManual(true);
    }

    // Update lowest/highest prices and the percent change
    private void updatePriceInfo() {
        // Grab textviews
        TextView priceLowest = (TextView) findViewById(R.id.price_lowest);
        TextView priceHighest = (TextView) findViewById(R.id.price_highest);
        TextView priceChange = (TextView) findViewById(R.id.price_percent_change);

        // Find prices from datapoints
        double starting_val = datapoints_.get(0).getY();
        double lowest_val = starting_val;
        double highest_val = starting_val;

        for (int i = 1; i < datapoints_.size(); i++) {
            if (datapoints_.get(i).getY() > highest_val) {
                highest_val = datapoints_.get(i).getY();
            }

            if (datapoints_.get(i).getY() < lowest_val) {
                lowest_val = datapoints_.get(i).getY();
            }
        }

        // Update the textviews
        priceLowest.setText("$" + DF.format(lowest_val));
        priceHighest.setText("$" + DF.format(highest_val));

        // Calculate the percent change
        if (starting_val < currentPriceDouble_) {
            double change = 100 * (currentPriceDouble_ - starting_val) / starting_val;
            priceChange.setText("+" + DF.format(change) + "%");
        } else if (starting_val > currentPriceDouble_){
            double change = 100 * (starting_val - currentPriceDouble_) / starting_val;
            priceChange.setText("-" + DF.format(change) + "%");
        } else {
            priceChange.setText("0.00%");
        }
    }

    public void logout(View view) {
        mAuth.signOut();
        toLogin();
    }

    private void toLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void toTrade(View view) {
        Intent intent = new Intent(this, TradeActivity.class);
        startActivity(intent);
    }
}
