package com.example.profilemanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

/**
 * This activity allows users to edit an existing diary entry.
 */

public class EditDiaryActivity extends AppCompatActivity {

    EditText editTitle, editDesc;

    Button saveBtn;

    String titleDiary, descDiary;

    DatabaseReference reference;

    String userId, entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        // Properly assign values from the intent
        userId = getIntent().getStringExtra("userId");
        entryId = getIntent().getStringExtra("entryId");
        titleDiary = getIntent().getStringExtra("title");
        descDiary = getIntent().getStringExtra("description");

        Log.d("EditDiary", "Received in EditDiaryActivity: Title=" + titleDiary + ", Desc=" + descDiary);

        reference = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("diaryEntries").child(entryId);

        editTitle = findViewById(R.id.editDiaryTitle);
        editDesc = findViewById(R.id.editDiaryDescription);
        saveBtn = findViewById(R.id.saveEditDiaryEntryButton);

        showData();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

    }

    /**
     * Displays the existing title and description in the EditText fields.
     */
    public void showData() {
        if (titleDiary != null && descDiary != null) {
            editTitle.setText(titleDiary);
            editDesc.setText(descDiary);
        } else {
            Toast.makeText(this, "Error: Entry data not found.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Saves changes to the diary entry in the database.
     */
    public void saveChanges() {
        String newTitle = editTitle.getText().toString();
        String newDesc = editDesc.getText().toString();
        Map<String, Object> updates = new HashMap<>();

        if (!titleDiary.equals(newTitle)) {
            updates.put("diaryTitle", newTitle);
            titleDiary = newTitle; // Update the local variable after successful database update
        }

        if (!descDiary.equals(newDesc)) {
            updates.put("diaryDesc", newDesc);
            descDiary = newDesc; // Update the local variable after successful database update
        }

        if (!updates.isEmpty()) {
            reference.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditDiaryActivity.this, "Data has been updated.", Toast.LENGTH_SHORT).show();
                        navigateToDiaryViewActivity();  // Navigate back to the DiaryViewActivity
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditDiaryActivity.this, "Failed to update data.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(EditDiaryActivity.this, "No changes detected.", Toast.LENGTH_SHORT).show();
            navigateToDiaryViewActivity();  // Navigate back even if no changes
        }
    }


    /**
     * Navigates back to the DiaryViewActivity after saving changes or if no changes were made.
     */
    public void navigateToDiaryViewActivity() {
        Intent intent = new Intent(EditDiaryActivity.this, DiaryViewActivity.class);
        intent.putExtra("username", userId);  // Pass any required data back
        startActivity(intent);
        finish();  // Close the current activity
    }


}