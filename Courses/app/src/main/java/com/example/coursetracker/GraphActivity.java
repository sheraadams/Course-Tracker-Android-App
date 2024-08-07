package com.example.coursetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    // Declare the pie chart
    private PieChart pieChart;

    // Declare the textView
    private TextView chartTextView;

    // get user information
    private User user = User.getInstance();
    private String username = user.getUsername();


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dataUpdateReceiver, new IntentFilter("com.example.coursetracker.DATA_UPDATED"), Context.RECEIVER_NOT_EXPORTED);
        refreshPieChart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dataUpdateReceiver);
    }

    // Broadcast Receiver to fetch updates
    private BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           
            // Handle broadcast receiver action to get updates
            if (intent != null && "com.example.coursetracker.DATA_UPDATED".equals(intent.getAction())) {
                refreshPieChart(); // Refresh the chart with updates
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // define custom colors
        int myTeal = ContextCompat.getColor(this, R.color.teal_200);

        // define the components of the gui
        pieChart = findViewById(R.id.pie_chart);
        chartTextView = findViewById(R.id.chart_title);

        // chart title
        chartTextView.setText("Percentage Completed");
        chartTextView.setTextColor(myTeal); // set the text color
        chartTextView.setTextSize(16f); // set the text size
        refreshPieChart();
    }

    private void refreshPieChart() {
        // Fetch the updated data and update the pie chart
        Intent intent = getIntent();
        if (intent != null) {
            
            // get the arraylist data from the main activity intent
            ArrayList<String> measurementTitles = intent.getStringArrayListExtra("title");
            ArrayList<String> measurementDescriptions = intent.getStringArrayListExtra("description");
            ArrayList<String> measurementPercents = intent.getStringArrayListExtra("percent");

            // Plot the data
            DBHelper myDb = new DBHelper(this);
            if (measurementTitles != null && measurementDescriptions != null && measurementPercents != null) {
                
                // calculate the percentage
                int totalRequired = 23;
                int totalCompletedBefore = myDb.getTotalCompleted(username);
                float percentageCompleted = ((float) totalCompletedBefore / totalRequired) * 100;

                // add entries and label
                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(percentageCompleted, "Percent Completed"));
                entries.add(new PieEntry(100 - percentageCompleted, "Percent Remaining"));
                PieDataSet courseData = new PieDataSet(entries, "");

                // set the pie chart slice colors
                courseData.setColors(ContextCompat.getColor(this, R.color.purple_200),
                        ContextCompat.getColor(this, R.color.purple_500));

                // specify the size and color of the labels
                PieData pieData = new PieData(courseData);
                pieData.setDrawValues(true);  // enable slice labels for the entries
                pieData.setValueTextSize(14); // set the font size
                pieData.setValueTextColor(Color.WHITE);  // set the font color

                // define the pie chart
                pieChart.setData(pieData);
                PieChart pieChart = findViewById(R.id.pie_chart);

                // set the description label color, set the message
                pieChart.getDescription().setTextColor(ContextCompat.getColor(this, R.color.purple_200));
                pieChart.getDescription().setTextSize(14); // set the size
                pieChart.getDescription().setText("\n Completed: "  + totalCompletedBefore + " / " + totalRequired);
                pieChart.setDrawHoleEnabled(false);  // disable the default center hole
                pieChart.getLegend().setTextSize(14); // set the text size and color
                pieChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.teal_200));
                pieChart.invalidate();
            }
            myDb.close();
        }
    }

}
