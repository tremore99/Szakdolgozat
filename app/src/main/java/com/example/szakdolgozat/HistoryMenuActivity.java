package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;

public class HistoryMenuActivity extends AppCompatActivity {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_menu);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_history_menu);
        listView = findViewById(R.id.valami);
        listView.setAdapter(adapter);
    }
}