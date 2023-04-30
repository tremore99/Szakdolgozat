package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class TrainingActivity extends AppCompatActivity {

    EditText mEditText;

    private FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        mEditText = findViewById(R.id.code);

        mStore = FirebaseFirestore.getInstance();
    }
}