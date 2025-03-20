package com.example.profilemanagementapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


/**
 * Allows users to edit their profile information including name, date of birth, address,
 * phone number, username, and password. Also provides functionality to delete the account.
 */

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editDOB, editAddress, editPhone, editUsername, editPassword;
    Button saveButton, deleteAccountButton;
    String nameUser, dobUser, addressUser, phoneUser, usernameUser, passwordUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        editName = findViewById(R.id.editName);
        editDOB = findViewById(R.id.editDob);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        deleteAccountButton = findViewById(R.id.deleteAccountBtn);

        showData();

        // Setup date picker for DOB
        editDOB.setFocusable(false);
        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nameChanged = isNameChanged();
                boolean dobChanged = isDOBChanged();
                boolean addressChanged = isAddressChanged();
                boolean phoneChanged = isPhoneChanged();
                boolean usernameChanged = isUsernameChanged();
                boolean passwordChanged = isPasswordChanged();  // This also handles navigation if password is changed and valid.

                if (passwordChanged) {
                    // If the password change was successful and valid, `isPasswordChanged()` will handle everything including navigation.
                    return; // Exit the method early.
                }

                if (nameChanged || dobChanged || addressChanged || phoneChanged || usernameChanged) {
                    // If any other change is made, navigate to ProfileView.
                    navigateToProfileView();
                    Toast.makeText(EditProfileActivity.this, "Changes saved successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    // No changes detected or only invalid password attempted.
                    Toast.makeText(EditProfileActivity.this, "No changes detected or password invalid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //deletes account
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

    }


    /**
     * Compares the new name with the current and updates Firebase if different.
     */
    public boolean isNameChanged() {
        if(!nameUser.equals(editName.getText().toString())) {
            reference.child(usernameUser).child("name").setValue(editName.getText().toString());
            nameUser = editName.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Compares the new dob with the current and updates Firebase if different.
     */
    public boolean isDOBChanged() {
        if(!dobUser.equals(editDOB.getText().toString())) {
            reference.child(usernameUser).child("dob").setValue(editDOB.getText().toString());
            dobUser = editDOB.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Displays a date picker dialog for selecting a new date of birth.
     */
    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Month is zero based in Calendar
                        monthOfYear = monthOfYear + 1;
                        String formattedDay = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        String formattedMonth = (monthOfYear < 10) ? "0" + monthOfYear : String.valueOf(monthOfYear);
                        String date = formattedDay + "/" + formattedMonth + "/" + year;
                        editDOB.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    /**
     * Compares the new address with the current and updates Firebase if different.
     */
    public boolean isAddressChanged() {
        if(!addressUser.equals(editAddress.getText().toString())) {
            reference.child(usernameUser).child("address").setValue(editAddress.getText().toString());
            addressUser = editAddress.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Compares the new phone number with the current and updates Firebase if different.
     */
    public boolean isPhoneChanged() {
        if(!phoneUser.equals(editPhone.getText().toString())) {
            reference.child(usernameUser).child("phone").setValue(editPhone.getText().toString());
            phoneUser = editPhone.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Compares the new username with the current and updates Firebase if different.
     */
    public boolean isUsernameChanged() {
        if(!usernameUser.equals(editUsername.getText().toString())) {
            reference.child(usernameUser).child("username").setValue(editUsername.getText().toString());
            usernameUser = editUsername.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks and updates the password if the new password meets the criteria.
     */
    public boolean isPasswordChanged() {
        String newPassword = editPassword.getText().toString();
        if (!passwordUser.equals(newPassword)) {
            if (isValidPassword(newPassword)) {
                reference.child(usernameUser).child("password").setValue(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) { // sends user to login screen if they change password
                                Toast.makeText(EditProfileActivity.this, "Password updated successfully. Please log in again.", Toast.LENGTH_SHORT).show();

                                // Redirect to LoginActivity
                                Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(loginIntent);
                                finish(); // Finish current activity to prevent returning to it
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                passwordUser = newPassword; // Update the local variable to new password
                return true; // Return true indicating a change has occurred
            } else {
                editPassword.setError("Password must be at least 8 characters long and include a digit and a special character.");
                return false; // Return false to indicate the password change was not accepted
            }
        }
        return false; // Return false if no change in password
    }


    /**
     * Validates the password based on defined criteria.
     * 8 characters long using both letters and numbers, at least one capital letter and one special character
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    /**
     * Loads user data into fields from Intent extras.
     */
    public void showData() {
        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        dobUser = intent.getStringExtra("dob");
        addressUser = intent.getStringExtra("address");
        phoneUser = intent.getStringExtra("phone");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");

        editName.setText(nameUser);
        editDOB.setText(dobUser);
        editAddress.setText(addressUser);
        editPhone.setText(phoneUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);
    }

    /**
     * Redirects to profile viewing activity after saving changes.
     */
    public void navigateToProfileView() {

        String userUsername = editUsername.getText().toString().trim();

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

                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);

                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("dob", dobFromDB);
                    intent.putExtra("address", addressFromDB);
                    intent.putExtra("phone", phoneFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);

                    startActivity(intent);
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EditProfileActivity", "Database error: " + error.getMessage());
            }
        });

    }

    /**
     * Confirms before deleting the user's account.
     * @param view The view that was clicked.
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null) // null will just close the dialog
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Deletes the user account from Firebase.
     */
    private void deleteAccount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(usernameUser);
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                // Log out the user and redirect to login screen or a suitable activity
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // This call is used to finish the current activity
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}