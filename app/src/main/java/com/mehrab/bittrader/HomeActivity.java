package com.mehrab.bittrader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<DataPoint> datapoints_ = new ArrayList<DataPoint>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        generateDatapoints();
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
