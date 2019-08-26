package com.mehrab.bittrader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<DataPoint> datapoints_ = new ArrayList<DataPoint>();
    private GraphView graph = (GraphView) findViewById(R.id.main_graph);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //generateDatapoints();
    }

    // Generates datapoints for datapoints_
    private void generateDatapoints() {
        for(int i = 0; i < 10; i++) {
            //datapoints_.add(new DataPoint(i, i));
        }
        updateGraph();
    }

    // Updates graph using the entries in datapoints_
    private void updateGraph() {
        //DataPoint[] datapoints = new DataPoint[datapoints_.size()];

        //for (int i = 0; i < datapoints_.size(); i++) {
          //  datapoints[i] = datapoints_.get(i);
        //}

        //graph.addSeries(new LineGraphSeries(datapoints));
    }
}
