package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.szakdolgozat.Object.TrackCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class CreateTrainingActivity extends AppCompatActivity {

    EditText mGeneratedCode;

    private FirebaseFirestore mStore;

    private FirebaseAuth mAuth;

    private CollectionReference mCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);

        mGeneratedCode = findViewById(R.id.GeneratedCode);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCodes = mStore.collection("TrackCodes");
    }

    public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public void Generate(View view) {
        mGeneratedCode.setText(getSaltString());
    }

    public void cancel(View view) {
        finish();
    }

    public void Create(View view) {
        String code = mGeneratedCode.getText().toString();
        mCodes.add(new TrackCode(mAuth.getCurrentUser().getUid(), code));
        Toast.makeText(CreateTrainingActivity.this, "Sikeresen létrehoztad a kódot", Toast.LENGTH_LONG).show();
        finish();
    }
}