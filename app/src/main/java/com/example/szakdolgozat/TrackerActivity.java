package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.szakdolgozat.databinding.ActivityTrackerBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTrackerBinding binding;
    private boolean isTrackingSessionRunning = false;
    private Button startStopButton;

    private LocationListener locationListener;

    private final long MinTime = 1000; // 1 second
    private final long MinDistance = 5; // 5 meters

    private LatLng latLng;

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

        // Get last known location and center the map on it
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                mMap.addMarker(new MarkerOptions().position(latLng));
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
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        };
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
    }

}