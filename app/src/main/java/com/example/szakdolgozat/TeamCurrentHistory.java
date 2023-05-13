package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.szakdolgozat.databinding.ActivityTeamCurrentHistoryBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamCurrentHistory extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTeamCurrentHistoryBinding binding;

    private FirebaseFirestore mStore;

    private FirebaseAuth mAuth;

    private List<String> trackDocumentId;

    private int colorCode;

    private String username;
    private List<HashMap> currentTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeamCurrentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        String code = bundle.getString("document_code");

        trackDocumentId = new ArrayList<>();

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mStore.collection("Tracks")
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("trackCode", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("LOGGER", "A mostani dokument id-ja: " + document.getId());
                                trackDocumentId.add(document.getId());
                            }
                            nextToDraw(0);
                        } else {
                            Log.d("LOGGER", "Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void nextToDraw(int i) {
        if (i < trackDocumentId.size()) {
            mStore.collection("Tracks")
                    .document(trackDocumentId.get(i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    currentTrack = (List<HashMap>) document.get("track");
                                    colorCode = document.get("userId").hashCode();
                                    getName(document.getString("userId"));
                                    nextToDraw(i + 1);
                                } else {
                                    Log.e("LOGGER", "No such document");
                                }
                            } else {
                                Log.e("LOGGER", "get failed with: " + task.getException());
                            }
                        }
                    });
        }
    }

    private void draw() {
        if (currentTrack.size() != 0) {
            PolylineOptions lineOptions = new PolylineOptions();
            int color = Color.HSVToColor(new float[]{colorCode % 360, 1, 1});
            lineOptions.color(color);
            lineOptions.width(5);
            LatLng latLng;
            LatLng first = null;
            LatLng last = null;

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
            mMap.addMarker(new MarkerOptions().position(first).title(username));
            mMap.addMarker(new MarkerOptions().position(last).title("Cél"));

            mMap.addPolyline(lineOptions);
        } else {
            finish();
        }
    }

    private void getName(String userUID) {
        mStore.collection("users")
                .whereEqualTo("userUID", userUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                username = (String) document.get("username");
                                draw();
                            }
                        }
                    }
                });
    }
}