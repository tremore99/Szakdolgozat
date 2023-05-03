package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TrainingActivity extends AppCompatActivity {

    EditText mEditText;

    private String code;

    private FirebaseFirestore mStore;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        mEditText = findViewById(R.id.code);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void confirm(View view) {
        code = mEditText.getText().toString();
        mStore.collection("Tracks")
                .whereEqualTo("trackCode", code)
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                findCode();
                            } else {
                                Toast.makeText(TrainingActivity.this, "Már használtad ezt a kódot", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void findCode() {
        mStore.collection("TrackCodes")
                .whereEqualTo("code", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Intent intent = new Intent(TrainingActivity.this, TrackerActivity.class);
                                    intent.putExtra("Track_code", code);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(TrainingActivity.this, "Helytelen kód", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void cancel(View view) {
        finish();
    }
}