package com.example.profilemanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This activity allows the user to create a new diary entry.
 * It provides fields to enter the title and description of the entry,
 * and buttons to save the entry or go back to the previous view.
 */

public class DiaryActivity extends AppCompatActivity {

    EditText diaryTitle, diaryDesc;
    Button saveDiaryBtn, backBtn;

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        diaryTitle = findViewById(R.id.diaryTitle);
        diaryDesc = findViewById(R.id.diaryDescription);
        saveDiaryBtn = findViewById(R.id.saveDiaryEntryButton);
        backBtn = findViewById(R.id.backButton);

        currentUser = getIntent().getStringExtra("username"); // gets the current user who is logged in


        saveDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = diaryTitle.getText().toString().trim();
                String description = diaryDesc.getText().toString().trim();
                if (!title.isEmpty() && !description.isEmpty()) { //if user has entered some characters for the title and description, add diary to the DB
                    addDiaryEntry(currentUser, title, description);
                    navigateToDiaryViewActivity(); // navigates to the DiaryViewActivity
                } else {
                    Toast.makeText(DiaryActivity.this, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToDiaryViewActivity(); // navigates to the DiaryViewActivity
            }
        });




    }


    /**
     * Adds a diary entry to the database under the current user.
     * @param userId The user ID under which the entry will be saved.
     * @param title The title of the diary entry.
     * @param description The description of the diary entry.
     */
    public void addDiaryEntry(String userId, String title, String description) {
        DiaryEntryClass diaryEntry = new DiaryEntryClass(title, description);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(userId).child("diaryEntries").push().setValue(diaryEntry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DiaryActivity.this, "Diary entry added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DiaryActivity.this, "Failed to add diary entry.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Navigates back to the DiaryViewActivity.
     */
    private void navigateToDiaryViewActivity() {
        Intent intent = new Intent(DiaryActivity.this, DiaryViewActivity.class);
        intent.putExtra("username", currentUser);
        startActivity(intent);
        finish(); // To ensure this activity is removed from the activity stack
    }







}