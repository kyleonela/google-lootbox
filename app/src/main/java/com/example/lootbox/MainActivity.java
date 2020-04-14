package com.example.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    Button mainLogoutButton, storeButton, myProfileButton, myInventoryButton;
    TextView welcomeText;

    /* Setup Firebase Auth and Firestore */
    FirebaseAuth fbAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLogoutButton = findViewById(R.id.mainLogoutButton);
        storeButton = findViewById(R.id.storeButton);
        myProfileButton = findViewById(R.id.myProfileButton);
        myInventoryButton = findViewById(R.id.myInventoryButton);
        welcomeText = findViewById(R.id.welcomeText);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        displayUserDetails(fbAuth, firestore);

        mainLogoutButton.setOnClickListener(x -> {
            logout();
        });

        storeButton.setOnClickListener(x -> {
            goToStore();
        });

        myProfileButton.setOnClickListener(x -> {
            goToMyProfile();
        });

        myInventoryButton.setOnClickListener(x -> {
            goToMyInventory();
        });
    }

    private void displayUserDetails(FirebaseAuth fbAuth, FirebaseFirestore firestore){
        String userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRef = firestore.collection("users").document(userId);

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                welcomeText.setText("Welcome, " + documentSnapshot.getString("firstName"));
            }
        });
    }

    private void goToStore() {
        startActivity(new Intent(getApplicationContext(), StoreActivity.class));
    }

    private void goToMyProfile() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void goToMyInventory() {
//        startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
    }

    /* Log out user from app */
    private void logout() {
        fbAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
