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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    private static final String PREF_KEY = LoginActivity.class.getPackage().toString();

    EditText userEmailET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailET = findViewById(R.id.userEditText);
        passwordET = findViewById(R.id.passwordEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String userName = userEmailET.getText().toString();
        String password = passwordET.getText().toString();

        Log.i(LOG_TAG, "Bejelentkezett: " + userName + ", jelszó: " + password);

        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                    MainMenu();
                } else {
                    Toast.makeText(LoginActivity.this, "Sikertelen bejelentkezés", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void MainMenu() {
        Intent intent = new Intent(this, UserMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", userEmailET.getText().toString());
        editor.putString("password", passwordET.getText().toString());

        editor.apply();
    }

    public void cancel(View view) {
        finish();
    }
}