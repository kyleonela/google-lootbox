package com.example.lootbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    /* Define variables */
    EditText firstNameInput, lastNameInput, emailInput, passwordInput, confirmPasswordInput;
    TextView signUpText;
    Button signUpButton;
    ProgressBar loadingBar;

    /* Setup Firebase Auth */
    FirebaseAuth fbAuth;

    /* Setup Firebase Firestore */
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Connecting variables to XML */
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpText = findViewById(R.id.signUpText);
        signUpButton = findViewById(R.id.signUpButton);
        loadingBar = findViewById(R.id.loadingBar);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(x -> {
            signUp();
        });
    }

    private void signUp() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String userId = fbAuth.getCurrentUser().getUid();

        /* Check if any field is empty */
        if (firstName.isEmpty()){
            firstNameInput.setError("Please enter your first name.");
            return;
        }
        if (lastName.isEmpty()){
            lastNameInput.setError("Please enter your last name.");
            return;
        }
        if (email.isEmpty()){
            emailInput.setError("Please enter your email.");
            return;
        }
        if (password.isEmpty()){
            passwordInput.setError("Please enter a password.");
            return;
        }
        if (confirmPassword.isEmpty()){
            confirmPasswordInput.setError("Please enter your password again.");
            return;
        }

        /* Check if passwords are the same */
        if (!password.equals(confirmPassword)){
            passwordInput.setError("Passwords must be the same!");
            confirmPasswordInput.setError("Passwords must be the same!");
            return;
        }

        /* Show the loading progress bar */
        loadingBar.setVisibility(View.VISIBLE);

        /* Register email and password with Firebase */
        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(create -> {
            if(create.isSuccessful()) {
                /* Create data in Firestore */
                DocumentReference docRef = firestore.collection("users").document(userId);

                HashMap<String, Object> user = new HashMap<>();
                user.put("firstName", firstName);
                user.put("lastName", lastName);
                user.put("email", email);

                docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Success", "Success!");
                    }
                });

                /* Display success message and start the main activity */
                Toast.makeText(RegisterActivity.this, "User has been created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                /* Display error message */
                Toast.makeText(RegisterActivity.this, "Failed to create a new user.", Toast.LENGTH_SHORT).show();
                loadingBar.setVisibility(View.INVISIBLE);
                return;
            }
        });
    }
}
