package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void Tracker(View view) {
        Intent intent = new Intent(this, TrackerActivity.class);
        startActivity(intent);
    }
}