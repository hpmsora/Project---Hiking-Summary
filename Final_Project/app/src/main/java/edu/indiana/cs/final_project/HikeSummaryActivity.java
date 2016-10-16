package edu.indiana.cs.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Map;

public class HikeSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hike_summary);

        TextView textViewSummaryHikeSummary = (TextView) findViewById(R.id.textView_Summary);
        textViewSummaryHikeSummary.setMovementMethod(new ScrollingMovementMethod());

        String All = "";

        int visited = 0;
        int notVisited = 0;

        SharedPreferences getAllDataPref = getSharedPreferences("savedLocation", MODE_PRIVATE);
        Map<String, ?> allEntries = getAllDataPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String contents = entry.getValue().toString();
            String[] contentsList = contents.split("%%%%");

            String content_title = contentsList[0];
            if(content_title.equals(""))
                content_title = "No Title";
            String content_description = contentsList[1];
            if(content_description.equals(""))
                content_description = "No Description";
            String content_temperature = contentsList[2];

            if(content_temperature.equals("Incomplete")) {
                notVisited++;
            } else {
                visited++;
            }

            String[] keyList = entry.getKey().split(",");
            String currentLocation = "Lat: " + keyList[0] + " Lng: " + keyList[1];

            String contentsTotal = content_title + "\n" + content_description + "\n\n" + content_temperature +"\n\n" + currentLocation + "\n\n";

            All += contentsTotal;
        }

        All = "Visited: " + visited + "  Not Visited : " + notVisited + "\n\n" + All;

        textViewSummaryHikeSummary.setText(All);
    }

    public void btnMap(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void btnToDoHikeSummary(View view) {
        startActivity(new Intent(this, ToDoListActivity.class));
    }
}
