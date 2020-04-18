package com.demo.lootbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    /* Define variables */
    EditText loginEmailInput, loginPasswordInput;
    TextView signUpText, forgotPasswordText;
    Button loginButton;
    ProgressBar loginLoadingBar;

    /* Setup Firebase Auth */
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        signUpText = findViewById(R.id.signUpText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        loginButton = findViewById(R.id.loginButton);
        loginLoadingBar = findViewById(R.id.loginLoadingBar);

        /* Getting current instance of database from Firebase */
        fbAuth = FirebaseAuth.getInstance();

        signUpText.setOnClickListener(x -> {
            register();
        });

        forgotPasswordText.setOnClickListener(y -> {
            forgotPassword();
        });

        loginButton.setOnClickListener(z -> {
            login();
        });
    }

    private void register() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void forgotPassword() {
        //TODO: IMPLEMENT RESET PASSWORD
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void login() {
        /* Error handling */
        String email = loginEmailInput.getText().toString();
        String password = loginPasswordInput.getText().toString();

        /* Check if any field is empty */
        if (email.isEmpty()){
            loginEmailInput.setError("Please enter your email.");
            return;
        }
        if (password.isEmpty()){
            loginPasswordInput.setError("Please enter your password.");
            return;
        }

        closeKeyboard();

        loginLoadingBar.setVisibility(View.VISIBLE);

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loggedIn -> {
            if(loggedIn.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                loginLoadingBar.setVisibility(View.INVISIBLE);
                return;
            }
        });
    }
}
