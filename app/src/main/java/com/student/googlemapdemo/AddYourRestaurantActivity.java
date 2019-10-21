package com.student.googlemapdemo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.student.googlemapdemo.util.DBHandler;

import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;

public class AddYourRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Marker myPosition;
    private EditText edtName, edtPhone;
    private TextView tvLat, tvLong;
    Button btnAdd;

    private DecimalFormat dFormat;
    private double selectedLat = 0, selectedLong = 0;
    private DBHandler dbHandler;
    private double myLat;
    private double myLong;
    private ImageView imgViewMyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_your_restaurant);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Restaurant");

        //Get the Views
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);
        imgViewMyLocation = findViewById(R.id.imgLocateMe);
        imgViewMyLocation.setOnClickListener(this);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        //decimanl formatter for LatLong
        dFormat = new DecimalFormat("#.######");

        //DBHandler instance
        dbHandler = new DBHandler(AddYourRestaurantActivity.this);

        //Get SupportMapFragment view instance and register for mapReadyCallback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get passed LatLong from previous screen
        myLat = getIntent().getDoubleExtra("lat", 22.3072);
        myLong = getIntent().getDoubleExtra("long", 73.1812);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);             //set type of the Map to display
        mMap.getUiSettings().setZoomControlsEnabled(true);      //display zoom controls on the map
        mMap.getUiSettings().setZoomGesturesEnabled(true);      //enable pinch zoom in-out on Map
        mMap.getUiSettings().setCompassEnabled(true);           //display compass with the map
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //enable map to display current locations

        //add current location marker on Map
        setCurrentLocationMarker(new LatLng(myLat, myLong));

        //handle onClickListener for Map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (edtName.getText().length() <= 0 || edtPhone.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Input name and phone", Toast.LENGTH_LONG).show();
                } else {

                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    if (mMap != null) {
                        mMap.clear();
                        mMap.addMarker(markerOptions);
                    }
                    selectedLat= Double.valueOf(dFormat.format(latLng.latitude));
                    selectedLong= Double.valueOf(dFormat.format(latLng.longitude));
                    tvLat.setText(String.valueOf(selectedLat));
                    tvLong.setText(String.valueOf(selectedLong));
                }
            }
        });
    }

    private void setCurrentLocationMarker(LatLng current) {

        if (myPosition != null) {
            myPosition.remove();
        }
        myPosition = mMap.addMarker(new MarkerOptions().position(current)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:

                if (selectedLat == 0 && selectedLong == 0 ) {
                    Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                }else{
                    String name = edtName.getText().toString();
                    String phone = edtPhone.getText().toString();
                    dbHandler.addNewRestaurant(new Restaurant(name, phone, selectedLat, selectedLong));
                    Toast.makeText(getApplicationContext(), "Location Added. Go back to see on map.", Toast.LENGTH_SHORT).show();
                }

                return;
            case R.id.imgLocateMe:
                moveToMyLocation();
                return;
            default:
                return;

        }
    }

    private void moveToMyLocation() {
        LatLng latLng = new LatLng(myLat, myLong);
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
