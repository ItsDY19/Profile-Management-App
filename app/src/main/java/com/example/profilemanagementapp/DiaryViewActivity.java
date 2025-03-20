package com.example.profilemanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * This activity displays a list of all diary entries for the current user.
 * Users can add new entries, edit existing ones, or delete them.
 */

public class DiaryViewActivity extends AppCompatActivity {

    private LinearLayout diaryEntriesContainer;
    private String currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_view);

        diaryEntriesContainer = findViewById(R.id.diaryEntriesContainer);
        Button addDiaryButton = findViewById(R.id.addDiaryButton);
        Button backBtn = findViewById(R.id.backDiaryButton);


        currentUser = getIntent().getStringExtra("username"); // current user who is logged in

        addDiaryButton.setOnClickListener(view -> {
            Intent intent = new Intent(DiaryViewActivity.this, DiaryActivity.class);
            intent.putExtra("username", currentUser); //sends logged in user data
            startActivity(intent); //sends user to DiaryActivity to add a diary
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadDiaryEntries(currentUser);
    }



    /**
     * Loads diary entries from Firebase and displays them in the UI.
     * @param userId The ID of the user whose diary entries are to be loaded.
     */
    private void loadDiaryEntries(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("diaryEntries"); //reference to firebase DB
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                diaryEntriesContainer.removeAllViews(); // Clear all views to prevent duplication

                // Iterate over all diary entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Inflate a new view for each diary entry using the predefined layout
                    View diaryView = LayoutInflater.from(DiaryViewActivity.this).inflate(R.layout.diary_entry_layout, diaryEntriesContainer, false);
                    TextView diaryText = diaryView.findViewById(R.id.diaryText);
                    Button editButton = diaryView.findViewById(R.id.editButton);
                    Button deleteButton = diaryView.findViewById(R.id.deleteButton);

                    DiaryEntryClass entry = snapshot.getValue(DiaryEntryClass.class);
                    if (entry != null) {
                        diaryText.setText(entry.getDiaryTitle() + "\n" + entry.getDiaryDesc());
                        editButton.setOnClickListener(v -> editDiaryEntry(entry, snapshot.getKey()));
                        deleteButton.setOnClickListener(v -> deleteDiaryEntry(snapshot.getKey()));
                    }

                    diaryEntriesContainer.addView(diaryView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DiaryViewActivity.this, "Failed to load diary entries.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Starts an activity to edit a specific diary entry.
     * @param entry The diary entry to be edited.
     * @param entryId The Firebase database key for the diary entry.
     */
    private void editDiaryEntry(DiaryEntryClass entry, String entryId) {
        // Intent to start DiaryActivity with existing entry data for editing
        Intent intent = new Intent(DiaryViewActivity.this, EditDiaryActivity.class);
        intent.putExtra("entryId", entryId); // Pass entry ID to edit
        intent.putExtra("title", entry.getDiaryTitle());
        intent.putExtra("description", entry.getDiaryDesc());
        intent.putExtra("userId", currentUser);
        startActivity(intent);
    }


    /**
     * Deletes a diary entry from Firebase and refreshes the view.
     * @param entryId The Firebase database key for the diary entry to be deleted.
     */
    private void deleteDiaryEntry(String entryId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUser).child("diaryEntries").child(entryId);
        reference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DiaryViewActivity.this, "Entry deleted successfully.", Toast.LENGTH_SHORT).show();
                    diaryEntriesContainer.removeAllViews(); // Clear the container
                    loadDiaryEntries(currentUser); // Reload entries
                })
                .addOnFailureListener(e -> Toast.makeText(DiaryViewActivity.this, "Failed to delete entry.", Toast.LENGTH_SHORT).show());
    }

}
