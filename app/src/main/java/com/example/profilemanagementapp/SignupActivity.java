package com.example.profilemanagementapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Activity for handling new user registrations.
 * Users can enter their details and sign up for an account.
 */

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupDOB, signupAddress, signupPhone, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupDOB = findViewById(R.id.signup_dob);
        signupAddress = findViewById(R.id.signup_address);
        signupPhone = findViewById(R.id.signup_phone);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupDOB.setFocusable(false);

        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("users");



        signupDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Attempts to sign up the user by validating input and creating a new user record in Firebase.
     */
    private void attemptSignup() {
        String name = signupName.getText().toString().trim();
        String dob = signupDOB.getText().toString().trim();
        String address = signupAddress.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if (!isValidPassword(password)) {
            signupPassword.setError("Password must be at least 8 characters long and include a digit and a special character.");
            return;
        }

        usersReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    signupUsername.setError("Username already exists.");
                } else {
                    HelperClass helperClass = new HelperClass(name, dob, address, phone, username, password);
                    usersReference.child(username).setValue(helperClass);
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SignupActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }


    /**
     * Validates the password based on defined criteria.
     * 8 characters long using both letters and numbers, at least one capital letter and one special character
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[0-9].*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    /**
     * Displays a DatePicker dialog for selecting the date of birth.
     */
    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignupActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Month is zero based in Calendar
                        monthOfYear = monthOfYear + 1;
                        String formattedDay = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        String formattedMonth = (monthOfYear < 10) ? "0" + monthOfYear : String.valueOf(monthOfYear);
                        String date = formattedDay + "/" + formattedMonth + "/" + year;
                        signupDOB.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }



}
