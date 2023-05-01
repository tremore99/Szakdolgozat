package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.szakdolgozat.Object.Track;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.szakdolgozat.databinding.ActivityTrackerBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class TrackerActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    private GoogleMap mMap;
    private ActivityTrackerBinding binding;
    private boolean isTrackingSessionRunning = false;
    private Button startStopButton;

    private LocationListener locationListener;

    private List<LatLng> utvonal;

    private final long MinTime = 1000; // 1 second
    private final long MinDistance = 0; // 0 meters

    private LatLng latLng;

    private Double distance = 0.0;

    private Double currentDistance = 0.0;

    private Double currentSpeed = 0.0;
    private Double allSpeed = 0.0;
    private Double maxSpeed = 0.0;
    private Double avgSpeed = 0.0;

    private Location locationA;
    private Location locationB;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mItems = mStore.collection("Tracks");

        startStopButton = findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTrackingSessionRunning) {
                    startTrackingSession();
                    isTrackingSessionRunning = true;
                    startStopButton.setText("Edzés befejezése");
                } else {
                    stopTrackingSession();
                    isTrackingSessionRunning = false;
                    startStopButton.setText("Edzés elkezdése");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.color(Color.RED);
        lineOptions.width(5);
        utvonal = new ArrayList<>();

        // Get last known location and center the map on it
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Itt vagy"));
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    lineOptions.add(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    mMap.addPolyline(lineOptions);
                    utvonal.add(latLng);
                    Log.i(LOG_TAG, "Mostani helyzeted: " + latLng);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private void calculateDistance() {
        for (int i = 0; i < utvonal.size()-1; i++) {
            locationA = new Location("point A");
            locationA.setLatitude(utvonal.get(i).latitude);
            locationA.setLongitude(utvonal.get(i).longitude);

            locationB = new Location("point B");
            locationB.setLatitude(utvonal.get(i+1).latitude);
            locationB.setLongitude(utvonal.get(i+1).longitude);
            distance += locationA.distanceTo(locationB);

            currentDistance = Double.valueOf(locationA.distanceTo(locationB));
            currentSpeed = (currentDistance * 1000) / (MinTime * 360);
            allSpeed += currentSpeed;
            if (currentSpeed > maxSpeed) {
                maxSpeed = currentSpeed;
            }
        }
        avgSpeed = allSpeed / utvonal.size()-1;
    }

    private void startTrackingSession() {
        // Register the LocationListener
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopTrackingSession() {
        // Unregister the LocationListener
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
        calculateDistance();
        mItems.add(new Track(mAuth.getCurrentUser().getUid(), utvonal, System.currentTimeMillis(), distance / 1000, avgSpeed, maxSpeed));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

}