package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.szakdolgozat.Object.UserLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    private DocumentReference user;

    EditText editUserName;
    EditText editEmail;

    private String username;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        editUserName = findViewById(R.id.username);
        editEmail = findViewById(R.id.userEmail);


        mStore.collection("users").whereEqualTo("userUID", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = mStore.collection("users").document(document.getId());
                                getUser();
                            }

                        } else {
                            Log.d("LOGGER", "Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    public void getUser() {
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        username = (String) document.get("username");
                        email = (String) document.get("email");

                        editUserName.setText(username);
                        editEmail.setText(email);
                    } else {
                        Log.e("LOGGER", "No such document");
                    }
                } else {
                    Log.e("LOGGER", "get failed with: " + task.getException());
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    public void update(View view) {
        user.update("username", editUserName.getText().toString());
        user.update("email", editEmail.getText().toString());
        mAuth.getCurrentUser().updateEmail(editEmail.getText().toString());
    }
}