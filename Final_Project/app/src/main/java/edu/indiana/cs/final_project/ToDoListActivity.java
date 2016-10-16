package edu.indiana.cs.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class ToDoListActivity extends AppCompatActivity {

    ArrayList<String> ls_location = new ArrayList<String>();
    ArrayList<String> ls_title = new ArrayList<String>();
    ArrayList<String> ls_temperature = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_to_do_list);

        //Create List
        //---------------------------------------------------------------------------------
        SharedPreferences mSavedLocation = getSharedPreferences("savedLocation", MODE_PRIVATE);
        Map<String, ?> allEntries = mSavedLocation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String content = entry.getValue().toString();
            ls_location.add(entry.getKey());
            String[] contentList = content.split("%%%%");
            ls_title.add(contentList[0]);
            ls_temperature.add(contentList[2]);
        }

        ArrayAdapter<String> taskAdapter = new taskListAdapter();
        ListView taskView = (ListView) findViewById(R.id.listViewTasks);
        taskView.setAdapter(taskAdapter);
        //---------------------------------------------------------------------------------

    }

    public void showingDetail(View view) {
        SharedPreferences saveCurrentView = getSharedPreferences("Current||Data", MODE_PRIVATE);
        SharedPreferences.Editor saveCurrentViewEditor = saveCurrentView.edit();

        TextView currentView = (TextView) view.findViewById(R.id.textView_LocationItemLayout);
        String currentKey = (String) currentView.getText();

        String[] mid_1_currentKeyList = currentKey.split("  ");
        currentKey = mid_1_currentKeyList[0].substring(5, mid_1_currentKeyList[0].length()) + "," + mid_1_currentKeyList[1].substring(5, mid_1_currentKeyList[1].length());

        saveCurrentViewEditor.putString("Current", currentKey);
        saveCurrentViewEditor.commit();

        startActivity(new Intent(this, TaskDetailActivity.class));
    }

    //Button Function
    //------------------------------------------------------------------------------------
    public void btnMapToDoActivity(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void btnHikeSummaryToDo(View view) {
        startActivity(new Intent(this, HikeSummaryActivity.class));
    }

    public void btnDeleteAll(View view) {

        SharedPreferences savedLocations = getSharedPreferences("savedLocation", MODE_PRIVATE);
        SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();
        savedLocationsEditor.clear();
        savedLocationsEditor.commit();

        startActivity(new Intent(ToDoListActivity.this, ToDoListActivity.class));
    }
    //------------------------------------------------------------------------------------


    //List item
    //------------------------------------------------------------------------------------
    private class taskListAdapter extends ArrayAdapter<String> {
        public taskListAdapter() {
            super(ToDoListActivity.this, R.layout.itemlayout, ls_title);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.itemlayout, parent, false);
            String titleString = ls_title.get(position);
            String temperatureString = ls_temperature.get(position);

            String[] locationStringList = ls_location.get(position).split(",");
            String locationString = "Lng: " + locationStringList[0] + "  Lng: " + locationStringList[1];

            TextView title_TextView = (TextView) itemView.findViewById(R.id.textView_Title);
            title_TextView.setText(titleString);

            TextView temperature_TextVeiw = (TextView) itemView.findViewById(R.id.textView_Temperature);
            temperature_TextVeiw.setText(temperatureString);

            TextView location_TextView = (TextView) itemView.findViewById(R.id.textView_LocationItemLayout);
            location_TextView.setText(locationString);

            return itemView;
        }
    }
    //------------------------------------------------------------------------------------
}
