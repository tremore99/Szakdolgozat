package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;


public class CurrentHistoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private ActivityCurrentHistoryBinding binding;

    private FirebaseFirestore mStore;

    private DocumentReference track;

    private Chip avgSpeed;

    private Chip maxSpeed;


    private List<HashMap> currentTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCurrentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        avgSpeed = findViewById(R.id.chip1);
        maxSpeed = findViewById(R.id.chip2);

        DecimalFormat df = new DecimalFormat("#.#");

        Bundle bundle = getIntent().getExtras();
        String secret_key = bundle.getString("Document_id");

        mStore = FirebaseFirestore.getInstance();

        track = mStore.collection("Tracks").document(secret_key);

        track.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        currentTrack = (List<HashMap>) document.get("track");
                        avgSpeed.setText("avg: " + df.format(document.get("avgSpeed")) + " km/h");
                        maxSpeed.setText("max: " + df.format(document.get("maxSpeed")) + " km/h");
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
    }

    public void draw() {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.color(Color.RED);
        lineOptions.width(5);
        LatLng latLng;
        LatLng first = null;
        LatLng last = null;

        if (currentTrack.size() == 0) {
            Toast.makeText(CurrentHistoryActivity.this, "Nincs semmilyen utvonal", Toast.LENGTH_LONG).show();
            finish();
        }

        for (int i = 0; i < currentTrack.size(); i++) {
            Double[] a = (Double[]) (currentTrack.get(i).values().toArray(new Double[currentTrack.get(i).size()]));
            latLng = new LatLng(a[0], a[1]);
            lineOptions.add(latLng);
            if (i == 0) {
                first = new LatLng(a[0], a[1]);
            } else if (i == currentTrack.size() - 1) {
                last = new LatLng(a[0], a[1]);
            }
        }

        if (currentTrack.size() == 1) {
            last = first;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(first, 16.0f));
        mMap.addMarker(new MarkerOptions().position(first).title("Start"));
        mMap.addMarker(new MarkerOptions().position(last).title("Cél"));

        mMap.addPolyline(lineOptions);
    }
}