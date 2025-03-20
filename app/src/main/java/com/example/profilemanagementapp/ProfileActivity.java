package com.example.profilemanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * Activity to display the user's profile information and provide navigation to edit profile and diary features.
 */
public class ProfileActivity extends AppCompatActivity {


    TextView profileName, profileDOB, profileAddress, profilePhone, profileUsername, profilePassword;
    TextView titleName, titleUsername;

    Button editProfile, logoutButton, diaryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        profileName = findViewById(R.id.profileName);
        profileDOB = findViewById(R.id.profileDOB);
        profileAddress = findViewById(R.id.profileAddress);
        profilePhone = findViewById(R.id.profilePhone);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        editProfile = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logoutButton);
        diaryButton = findViewById(R.id.diaryButton);

        // display user data in profile view
        showUserData();



        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDiaryActivity();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

    }

    /**
     * Displays user data retrieved from the intent extras.
     */
    public void showUserData() {

        Intent intent  = getIntent();

        String nameUser = intent.getStringExtra("name");
        String dobUser = intent.getStringExtra("dob");
        String addressUser = intent.getStringExtra("address");
        String phoneUser = intent.getStringExtra("phone");
        String usernameUser = intent.getStringExtra("username");
        String passwordUser = intent.getStringExtra("password");

        titleName.setText(nameUser);
        titleUsername.setText(usernameUser);
        profileName.setText(nameUser);
        profileDOB.setText(dobUser);
        profileAddress.setText(addressUser);
        profilePhone.setText(phoneUser);
        profileUsername.setText(usernameUser);
        profilePassword.setText(passwordUser);


    }


    /**
     * Passes the user data to the EditProfileActivity for editing.
     */
    public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                    String dobFromDB = userSnapshot.child("dob").getValue(String.class);
                    String addressFromDB = userSnapshot.child("address").getValue(String.class);
                    String phoneFromDB = userSnapshot.child("phone").getValue(String.class);
                    String usernameFromDB = userSnapshot.child("username").getValue(String.class);
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("dob", dobFromDB);
                    intent.putExtra("address", addressFromDB);
                    intent.putExtra("phone", phoneFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);

                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileActivity", "Database error: " + error.getMessage());
            }
        });

    }

    /**
     * Opens the DiaryViewActivity to view and manage the user's diary entries.
     */
    private void openDiaryActivity() {
        String usernameUser = profileUsername.getText().toString().trim();
        Intent intent = new Intent(ProfileActivity.this, DiaryViewActivity.class);
        intent.putExtra("username", usernameUser);
        startActivity(intent);
    }


    /**
     * Logs out the user and redirects to the LoginActivity.
     */
    private void logoutUser() {

        // Redirect to login screen
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}