package com.student.googlemapdemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.student.googlemapdemo.util.CustomInfoWindowAdapter;
import com.student.googlemapdemo.util.DBHandler;
import com.student.googlemapdemo.util.DisplayToast;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long LOCATION_REFRESH_TIME = 1000;
    private static final float LOCATION_REFRESH_DISTANCE = 15;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;
    private Location location, myLocation;
    private MarkerOptions currentMarkerOption;
    private Marker myPosition;
    private LocationListener locationListener;
    private FloatingActionButton addPlace;
    private DBHandler dbHandler;
    private CustomInfoWindowAdapter customInfoWindowAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);

        dbHandler = new DBHandler(HomeMapActivity.this);

        //Set activity toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Google Map Demo - current location");
        setSupportActionBar(toolbar);

        //Get SupportMapFragment view instance and register for mapReadyCallback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Ask for LocationPermission to get granted
        checkLocationPermission();

        //Initialize Location Change listener
        //this will work when you move around
        //to check this try to walk around 25mtr here/there
        initializeLocationListener();

        //get instance of LocationManager to get lastKnownLocation
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get the provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);

        //attach locationManager with locationListener to get periodic updated about location changes
        locationManager.requestLocationUpdates(provider, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);

        addPlace = findViewById(R.id.fab);
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    Intent intent = new Intent(HomeMapActivity.this, AddYourRestaurantActivity.class);
                    intent.putExtra("lat", myLocation.getLatitude());
                    intent.putExtra("long", myLocation.getLongitude());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "let me get your location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
     * Callback when Google Map is ready to Use
     * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);             //set type of the Map to display
        mMap.getUiSettings().setZoomControlsEnabled(true);      //display zoom controls on the map
        mMap.getUiSettings().setZoomGesturesEnabled(true);      //enable pinch zoom in-out on Map
        mMap.getUiSettings().setCompassEnabled(true);           //display compass with the map
        mMap.getUiSettings().setMyLocationButtonEnabled(false); //Prevent map to display current location

        if (provider == null) {
            Log.i("ProviderNull", "onMapReady");
            return;
        }

        checkLocationPermission();
        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            setCurrentMarker(location, "My Location");
        } else {
            Log.i("Location Info", "No location :(");
        }

        //InfoWindowAdapter
        customInfoWindowAdapter = new CustomInfoWindowAdapter(HomeMapActivity.this);
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);

        //getAllStoredLocation
        getAndLoadLocations();
    }

    private void getAndLoadLocations() {
        List<Restaurant> restaurantList = dbHandler.getAllRestaurantList();
        //This is the current user-viewable region of the map
        for (Restaurant restaurant : restaurantList) {
            MarkerOptions stored = new MarkerOptions();
            LatLng restaurantLatLong = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            stored.position(restaurantLatLong);
            stored.title(restaurant.getName());
            stored.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            //customInfoWindowAdapter
            Marker marker = mMap.addMarker(stored);
            marker.setTag(restaurant);
            marker.showInfoWindow();
        }
    }

    /*
     * Method that will display marker for specific location
     * */
    private void setCurrentMarker(Location loc, String markerTitle) {
        myLocation = loc;
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        currentMarkerOption = new MarkerOptions().position(latLng).title(markerTitle);
        currentMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        if (mMap != null) {
            if (myPosition != null) {
                myPosition.remove();
            }
            myPosition = mMap.addMarker(currentMarkerOption);
            myPosition.setTag(new Restaurant("My Location", "9998970653", loc.getLatitude(), loc.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            (new DisplayToast("mMap Null", Toast.LENGTH_LONG, getApplicationContext())).display();
        }
    }

    /*
     * This method checks weather Location access permission is granted or not
     * If not granted then Ask for the permission
     * */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /*
     * CallBack for requested Permission
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        if (locationManager != null) {
                            locationManager.requestLocationUpdates(provider, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    (new DisplayToast("You must grant location permission to run this app", Toast.LENGTH_LONG, getApplicationContext())).display();
                    finish();
                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
        }
        if (mMap != null) {
            getAndLoadLocations();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    /*
     * Private Class for LocationListener
     * */
    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location changedLocation) {
                Log.d("LocationListener", "onLocationChanged: " + changedLocation.getLatitude() + "_" + changedLocation.getLongitude());
                if (changedLocation != null) {
                    if (myLocation == null) {
                        setCurrentMarker(changedLocation, "My Location");
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


}