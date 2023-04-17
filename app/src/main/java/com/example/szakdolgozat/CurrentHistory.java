package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.szakdolgozat.Object.Track;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.szakdolgozat.databinding.ActivityCurrentHistoryBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentHistory extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCurrentHistoryBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    private DocumentReference track;

    private List<HashMap> currentTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCurrentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        track = mStore.collection("Tracks").document("70zqed0ohsEJxB4bN2Q0");

        track.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        currentTrack = (List<HashMap>) document.get("track");
                        draw();
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
    }

    public void draw() {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.color(Color.RED);
        lineOptions.width(5);
        LatLng latLng;

        Log.i("LOGGED", currentTrack.get(0).values().toString());

        for (int i = 0; i < currentTrack.size(); i++) {
            Double[] a = (Double[])(currentTrack.get(i).values().toArray(new Double[currentTrack.get(i).size()]));
            latLng = new LatLng(a[0], a[1]);
            lineOptions.add(latLng);
        }

        mMap.addPolyline(lineOptions);
    }

}