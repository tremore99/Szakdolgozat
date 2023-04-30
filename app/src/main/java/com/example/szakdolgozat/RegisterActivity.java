package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.szakdolgozat.Object.UserLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText userPasswordAgainEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userPasswordAgainEditText = findViewById(R.id.userPasswordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(email);
        userPasswordEditText.setText(password);
        userPasswordAgainEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mItems = mStore.collection("users");

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String userEmail = userEmailEditText.getText().toString();
        String password = userPasswordEditText.getText().toString();
        String passwordAgain = userPasswordAgainEditText.getText().toString();

        if (!password.equals(passwordAgain)) {
            Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik meg", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "User created successfully");
                        Toast.makeText(RegisterActivity.this, "Felhasználó sikeresen létrehozva", Toast.LENGTH_LONG).show();
                        mItems.add(new UserLayout(userEmail, password, userName, mAuth.getCurrentUser().getUid()));
                        MainMenu();
                    } else {
                        Log.d(LOG_TAG, "User wasnt created");
                        Toast.makeText(RegisterActivity.this, "Felhasználó létrehozása sikertelen" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        Log.i(LOG_TAG, "Regisztrált: " + userName + ", email: " + userEmail);
    }

    public void cancel(View view) {
        finish();
    }

    public void MainMenu() {
        Intent intent = new Intent(this, UserMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", userNameEditText.getText().toString());
        editor.putString("email", userEmailEditText.getText().toString());
        editor.putString("password", userPasswordEditText.getText().toString());
        editor.putString("passwordAgain", userPasswordAgainEditText.getText().toString());

        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}