GoogleMap mMap;
TextView mText;
SupportMapFragment mapFragment;

Location currLocation;
LatLng currLatLng;
Button getLoc;

protected LocationManager locationManager;
protected LocationListener locationListener;
protected boolean isStarted = true, network_enabled;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    }

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

