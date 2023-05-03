package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryMenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    private List<Date> trackDateList;

    private List<Double> trackDistanceList;

    private List<String> trackDocumentId;

    private Date currentDate;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_menu);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        trackDateList = new ArrayList<>();
        trackDistanceList = new ArrayList<>();
        trackDocumentId = new ArrayList<>();

        mStore.collection("Tracks")
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
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
        listView = findViewById(R.id.valami);
    }

    private void nextToDraw(int i) {
        if (i < trackDocumentId.size()) {
            mStore.collection("Tracks").document(trackDocumentId.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            currentDate = new Date((Long) document.get("date"));
                            trackDateList.add(currentDate);
                            trackDistanceList.add((Double) document.get("distance"));
                            draw();
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
        ArrayAdapter<Date> adapter = new ArrayAdapter<Date>(this, R.layout.historycardview, R.id.trackDate, trackDateList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                DecimalFormat df = new DecimalFormat("#.##");
                DateFormat dateFormat = new SimpleDateFormat("dd.MMMM.yyyy");

                TextView textView1 = view.findViewById(R.id.trackDate);
                TextView textView2 = view.findViewById(R.id.trackDistance);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistoryMenuActivity.this, CurrentHistoryActivity.class);
                        intent.putExtra("Document_id", trackDocumentId.get(position));
                        startActivity(intent);
                    }
                });

                textView1.setText(dateFormat.format(trackDateList.get(position)));
                textView2.setText(df.format(trackDistanceList.get(position)) + "km");

                return view;
            }
        };
        listView.setAdapter(adapter);
    }
}