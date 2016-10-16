package edu.indiana.cs.final_project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        SensorEventListener {

    GoogleMap mMap;
    TextView mText;
    SupportMapFragment mapFragment;

    Location currLocation;
    LatLng currLatLng;
    Button getLoc;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected boolean isStarted = true, network_enabled;

    private SensorManager sensorManager;
    private Sensor temperatureSensor;

    String currTemperature = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mText = (TextView) findViewById(R.id.textViewForLngLat);
        getLoc = (Button) findViewById(R.id.btn_ToDoList);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        //Sensor Declaration
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    }


    //Map Function
    //------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);

        SharedPreferences mSavedLocation = getSharedPreferences("savedLocation", MODE_PRIVATE);
        Map<String, ?> allEntries = mSavedLocation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String locations = entry.getKey();
            String[] locationsList = locations.split(",");

            String contents = entry.getValue().toString();
            String[] contentsList = contents.split("%%%%");

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Float.parseFloat(locationsList[0]), Float.parseFloat(locationsList[1])))
                    .title(contentsList[0])
                    .snippet(entry.getKey())
            );
        }

        mMap.setOnMarkerClickListener(this);

        //Map On Click Part
        //---------------------------------------------------------------------------------
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mText.setText("Lat: " + String.valueOf(latLng.latitude) + " Lng: " + String.valueOf(latLng.longitude));

                String data = latLng.latitude + "," + latLng.longitude;

                SharedPreferences savingLocation = getSharedPreferences("savedLocation", MODE_PRIVATE);
                SharedPreferences.Editor savingLocationEditor = savingLocation.edit();
                savingLocationEditor.putString(data, "Task!%%%%%%%%Incomplete");
                savingLocationEditor.commit();

                mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Task!"))
                .setSnippet(data);
            }
        });
        //---------------------------------------------------------------------------------

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        SharedPreferences saveCurrentView = getSharedPreferences("Current||Data", MODE_PRIVATE);
        SharedPreferences.Editor saveCurrentViewEditor = saveCurrentView.edit();

        saveCurrentViewEditor.putString("Current", marker.getSnippet());
        saveCurrentViewEditor.commit();

        mText.setText(marker.getSnippet());

        startActivity(new Intent(this, TaskDetailActivity.class));
        return true;
    }

    private void goToLocationZoom(double lat, double lng, int zoom) {
        LatLng place = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoom));
    }
    //------------------------------------------------------------------------------------


    //Location Function
    //------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {

        currLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        TextView textView_Current_Location = (TextView) findViewById(R.id.textViewCurrentLocation);
        textView_Current_Location.setText("Lat: " + String.valueOf(location.getLatitude()) + " Lng: " + String.valueOf(location.getLongitude()));

        if(isStarted) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currLatLng, 16);
            mMap.animateCamera(update);

            isStarted = false;
        }

        SharedPreferences mSavedLocation = getSharedPreferences("savedLocation", MODE_PRIVATE);
        Map<String, ?> allEntries = mSavedLocation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String currentKey = entry.getKey();
            String[] currentKeyList = currentKey.split(",");

            Location savedLocation = new Location("");
            savedLocation.setLatitude(Float.parseFloat(currentKeyList[0]));
            savedLocation.setLongitude(Float.parseFloat(currentKeyList[1]));

            if (location.distanceTo(savedLocation) < 100) {
                sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);

                SharedPreferences.Editor mSavedLocationEditor = mSavedLocation.edit();
                String mContents = mSavedLocation.getString(entry.getKey(), "N/A");
                if(!mContents.equals("N/A")){
                    String[] mContentsList = mContents.split("%%%%");
                    String newData = mContents;
                    if(currTemperature == null || currTemperature.equals("Sensor Not Working")){
                        newData = mContentsList[0] + "%%%%" + mContentsList[1] + "%%%%" + "Sensor Not Working";
                    } else {
                        if(mContentsList[2] != "") {
                            if(!(mContentsList[2].equals("Sensor Not Working") || mContentsList[2].equals("Incomplete"))) {
                                Float originalTemperature = Float.parseFloat(mContentsList[2]);
                                Float currentTemperature = Float.parseFloat(currTemperature);
                                Toast.makeText(this, "Temperature Difference: " + Float.toString(originalTemperature - currentTemperature), Toast.LENGTH_LONG).show();
                            }
                        }
                        newData = mContentsList[0] + "%%%%" + mContentsList[1] + "%%%%" + currTemperature;
                    }
                    mSavedLocationEditor.putString(entry.getKey(), newData);
                    mSavedLocationEditor.commit();
                }
                if(temperatureSensor != null)
                    sensorManager.unregisterListener(this);
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "Enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "Diable");
    }
    //------------------------------------------------------------------------------------


    //Button Function
    //------------------------------------------------------------------------------------
    public void btnToDo(View view) {
        startActivity(new Intent(this, ToDoListActivity.class));
    }

    public void btnHikeSummary(View view) {
        startActivity(new Intent(this, HikeSummaryActivity.class));
    }
    //------------------------------------------------------------------------------------


    //Sensor Function
    //------------------------------------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent event) {

        Float value = event.values[0];
        if(value != null)
            currTemperature = Float.toString(value);
        else
            currTemperature = "Sensor Not Working";

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //------------------------------------------------------------------------------------

}
