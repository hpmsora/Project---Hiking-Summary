package edu.indiana.cs.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.vision.text.Text;

public class TaskDetailActivity extends AppCompatActivity {

    String currentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_detail);

        //Fill the original Data
        //---------------------------------------------------------------------------------
        SharedPreferences currentData = getSharedPreferences("Current||Data", MODE_PRIVATE);
        currentKey = currentData.getString("Current", "N/A");

        if(currentKey.equals("N/A")) {
            Toast.makeText(this, "The Data Not Exist!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, ToDoListActivity.class));
            return;
        }

        SharedPreferences mSavedLocation = getSharedPreferences("savedLocation", MODE_PRIVATE);
        String currentDataContents = mSavedLocation.getString(currentKey, "N/A");

        TextView textViewLocation = (TextView) findViewById(R.id.textView_LanLng);
        String[] currentKeyList = currentKey.split(",");
        textViewLocation.setText("Lng: " + currentKeyList[0] + " Lng: " + currentKeyList[1]);

        String[] currentDataContentsList = currentDataContents.split("%%%%");

        EditText title_EditTextField = (EditText) findViewById(R.id.editText_TitleField);
        title_EditTextField.setText(currentDataContentsList[0]);

        EditText description_EditTextField = (EditText) findViewById(R.id.editText_DescriptionField);
        description_EditTextField.setText(currentDataContentsList[1]);

        TextView textViewTemperature = (TextView) findViewById(R.id.textView_TemperatureNow);
        textViewTemperature.setText("Temperature: " + currentDataContentsList[2]);
        //---------------------------------------------------------------------------------

    }

    //Button Function
    //------------------------------------------------------------------------------------
    public void btnBackTaskDetail(View view) {
        startActivity(new Intent(this, ToDoListActivity.class));
    }

    public void btnSaveTaskDetail(View view) {
        EditText titleEditText = (EditText) findViewById(R.id.editText_TitleField);
        EditText descriptionEditText = (EditText) findViewById(R.id.editText_DescriptionField);
        TextView temperatureTextView = (TextView) findViewById(R.id.textView_TemperatureNow);

        String allTogether = titleEditText.getText().toString() + "%%%%" + descriptionEditText.getText().toString() + "%%%%" + temperatureTextView.getText().toString().substring(13, temperatureTextView.getText().toString().length());

        SharedPreferences savedData = getSharedPreferences("savedLocation", MODE_PRIVATE);
        SharedPreferences.Editor savedDataEditor = savedData.edit();
        savedDataEditor.putString(currentKey, allTogether);
        savedDataEditor.commit();

        startActivity(new Intent(this, ToDoListActivity.class));
    }

    public void btnDeleteTaskDetail(View view) {
        SharedPreferences currentData = getSharedPreferences("Current||Data", MODE_PRIVATE);
        SharedPreferences savedData = getSharedPreferences("savedLocation", MODE_PRIVATE);
        SharedPreferences.Editor savedDataEditor = savedData.edit();
        String currentKey = currentData.getString("Current", "N/A");
        savedDataEditor.remove(currentKey);
        savedDataEditor.commit();

        startActivity(new Intent(this, ToDoListActivity.class));
    }
    //------------------------------------------------------------------------------------
}
