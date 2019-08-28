package com.mehrab.bittrader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity.java";
    private RequestQueue queue;
    private static final String CURRENT_PRICE_URL =
            "https://api.coindesk.com/v1/bpi/currentprice.json";
    private static final String HISTORICAL_PRICE_URL =
            "https://api.coindesk.com/v1/bpi/historical/close.json";



    private List<DataPoint> datapoints_ = new ArrayList<DataPoint>();
    private String currentPrice_ = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        updatePrice();
        generateDatapoints();
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
                        try {
                            JSONObject Usd = Bpi.getJSONObject("USD");
                            currentPrice_ = "$" + Usd.getString("rate");

                            // Update price
                            TextView btc_price = (TextView) findViewById(R.id.btc_price);
                            btc_price.setText(currentPrice_);
                        } catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG, "rate not found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "BPI not found");
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
    // Generates datapoints for datapoints_
    private void generateDatapoints() {
        for(int i = 0; i < 10; i++) {
            datapoints_.add(new DataPoint(i, i));
        }
        updateGraph();
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
    }

}
