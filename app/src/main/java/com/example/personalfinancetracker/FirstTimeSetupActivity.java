package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FirstTimeSetupActivity extends AppCompatActivity {

    private static final String TAG = "FirstTimeSetupActivity";
    private EditText nameEditText;
    private EditText initialBalanceEditText;
    private Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_setup);

        // Log lifecycle event
        Log.d(TAG, "onCreate() called");

        // Initialize UI components
        nameEditText = findViewById(R.id.edit_name);
        initialBalanceEditText = findViewById(R.id.edit_initial_balance);
        currencySpinner = findViewById(R.id.spinner_currency);
        Button proceedButton = findViewById(R.id.button_proceed);

        // Load previously saved name if returning from low balance scenario
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        String savedName = prefs.getString("userName", "");
        if (!savedName.isEmpty()) {
            nameEditText.setText(savedName);
        }

        // Set up currency spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        // Set up proceed button click handler
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        // Restore state if available
        if (savedInstanceState != null) {
            nameEditText.setText(savedInstanceState.getString("name", ""));
            initialBalanceEditText.setText(savedInstanceState.getString("initialBalance", ""));
            currencySpinner.setSelection(savedInstanceState.getInt("currencyPosition", 0));
        }
    }

    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        String initialBalanceStr = initialBalanceEditText.getText().toString().trim();
        String currency = currencySpinner.getSelectedItem().toString();

        // Validate inputs
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }

        if (initialBalanceStr.isEmpty()) {
            initialBalanceEditText.setError("Initial balance is required");
            return;
        }

        try {
            double initialBalance = Double.parseDouble(initialBalanceStr);
            if (initialBalance < 0) {
                initialBalanceEditText.setError("Balance cannot be negative");
                return;
            }

            // Save user data to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", name);
            editor.putFloat("initialBalance", (float) initialBalance);
            editor.putFloat("currentBalance", (float) initialBalance);
            editor.putString("currency", currency);
            editor.putFloat("spendingAmount", 0);
            editor.apply();

            Toast.makeText(this, "Setup completed successfully", Toast.LENGTH_SHORT).show();

            // Navigate to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (NumberFormatException e) {
            initialBalanceEditText.setError("Invalid balance format");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("initialBalance", initialBalanceEditText.getText().toString());
        outState.putInt("currencyPosition", currencySpinner.getSelectedItemPosition());
        Log.d(TAG, "onSaveInstanceState() called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}


