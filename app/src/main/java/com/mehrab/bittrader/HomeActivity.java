package com.mehrab.bittrader;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
        updateDatapoints();
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

                        // Update price
                        TextView btc_price = (TextView) findViewById(R.id.btc_price);
                        btc_price.setText(currentPrice_);

                        // FInd the time of update
                        JSONObject Time = response.getJSONObject("time");
                        TextView last_updated = (TextView) findViewById(R.id.last_updated);
                        last_updated.setText("Updated: " + Time.getString("updated"));
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
                        updateGraph();
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
}
