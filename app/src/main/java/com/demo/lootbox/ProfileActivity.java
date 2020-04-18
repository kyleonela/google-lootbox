package com.demo.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    TextView yourNameText, profilePhoneText, profileEmailText, profileAddressText;
    ImageView profilePictureImage;

    /* Setup Firebase Auth and Firestore */
    FirebaseAuth fbAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        yourNameText = findViewById(R.id.yourNameText);
        profilePhoneText = findViewById(R.id.profilePhoneText);
        profileEmailText = findViewById(R.id.profileEmailText);
        profileAddressText = findViewById(R.id.profileAddressText);
        profilePictureImage = findViewById(R.id.profilePictureImage);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        displayUserDetails(fbAuth, firestore);
    }

    private void displayUserDetails(FirebaseAuth fbAuth, FirebaseFirestore firestore){
        String userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRef = firestore.collection("users").document(userId);

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //add more fields whenever
                String fullName = String.format("%s %s", documentSnapshot.getString("firstName"), documentSnapshot.getString("lastName"));
                yourNameText.setText(fullName);
                profileEmailText.setText(documentSnapshot.getString("email"));
            }
        });
    }
}
