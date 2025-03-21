package com.example.profilemanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import java.util.Objects;


/**
 * Activity responsible for handling user login.
 * It checks user input against credentials stored in Firebase to authenticate users.
 */

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this); //initialize Firebase

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                }
                else {
                    checkUser();
                }
            }
        });

        // Redirect to signup activity if user taps on signup text
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Validates the username input field.
     * @return true if the username is not empty, false otherwise.
     */
    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);  // Clear any previous error
            return true;
        }
    }

    /**
     * Validates the password input field.
     * @return true if the password is not empty, false otherwise.
     */
    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);  // Clear any previous error
            return true;
        }
    }

    /**
     * Verifies the user's credentials against those stored in Firebase.
     */
    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Log.d("LoginActivity", "Snapshot: " + snapshot.getValue());

                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    // Correcting the path to access the password
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                    Log.d("Login", "DB Password: " + passwordFromDB + ", Entered Password: " + userPassword);

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {

                        Log.d("Login", "Password match, starting ProfileActivity.");
                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String dobFromDB = snapshot.child(userUsername).child("dob").getValue(String.class);
                        String addressFromDB = snapshot.child(userUsername).child("address").getValue(String.class);
                        String phoneFromDB = snapshot.child(userUsername).child("phone").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);



                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("dob", dobFromDB);
                        intent.putExtra("address", addressFromDB);
                        intent.putExtra("phone", phoneFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);


                        startActivity(intent);
                    } else {
                        Log.d("Login", "Password mismatch or null.");
                        loginPassword.setError("Incorrect username or password");
                        loginUsername.setError("Incorrect username or password");
                    }
                } else {
                    loginUsername.setError("Incorrect username or password");
                    loginPassword.setError("Incorrect username or password");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("LoginActivity", "Firebase error: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Failed to check credentials: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}