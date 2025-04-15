
package com.example.personalfinancetracker;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private static final String TAG = "AddTransactionActivity";
    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button datePickerButton;
    private EditText notesEditText;
    private Calendar selectedDate;
    private String currencySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Log lifecycle event
        Log.d(TAG, "onCreate() called");

        // Initialize UI components
        amountEditText = findViewById(R.id.edit_transaction_amount);
        categorySpinner = findViewById(R.id.spinner_category);
        datePickerButton = findViewById(R.id.button_date_picker);
        notesEditText = findViewById(R.id.edit_transaction_notes);
        Button saveButton = findViewById(R.id.button_save_transaction);

        // Set up navigation buttons
        Button dashboardButton = findViewById(R.id.button_dashboard);
        Button addTransactionButton = findViewById(R.id.button_add_transaction);
        Button viewReportsButton = findViewById(R.id.button_view_reports);

        // Get currency symbol
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        String currency = prefs.getString("currency", "USD ($)");
        if (currency.contains("$")) {
            currencySymbol = "$";
        } else if (currency.contains("€")) {
            currencySymbol = "€";
        } else if (currency.contains("£")) {
            currencySymbol = "£";
        } else if (currency.contains("¥")) {
            currencySymbol = "¥";
        } else if (currency.contains("₹")) {
            currencySymbol = "₹";
        } else {
            currencySymbol = "$"; // Default
        }

        dashboardButton.setOnClickListener(v -> {
            finish();
        });

        addTransactionButton.setOnClickListener(v -> {
            // Already on add transaction, do nothing
        });

        viewReportsButton.setOnClickListener(v -> {
            finish();
            getApplicationContext().startActivity(
                    getPackageManager().getLaunchIntentForPackage(getPackageName())
                            .setClassName(getPackageName(), ViewReportsActivity.class.getName())
            );
        });

        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Initialize date picker with current date
        selectedDate = Calendar.getInstance();
        updateDateButtonText();

        // Set up date picker button click listener
        datePickerButton.setOnClickListener(v -> showDatePicker());

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveTransaction());

        // Restore state if available
        if (savedInstanceState != null) {
            amountEditText.setText(savedInstanceState.getString("amount", ""));
            categorySpinner.setSelection(savedInstanceState.getInt("categoryPosition", 0));
            selectedDate.setTimeInMillis(savedInstanceState.getLong("selectedDate", System.currentTimeMillis()));
            notesEditText.setText(savedInstanceState.getString("notes", ""));
            updateDateButtonText();
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());
        datePickerButton.setText(formattedDate);
    }

    private void saveTransaction() {
        String amountStr = amountEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String notes = notesEditText.getText().toString().trim();

        // Validate inputs
        if (amountStr.isEmpty()) {
            amountEditText.setError("Amount is required");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                amountEditText.setError("Amount must be positive");
                return;
            }

            // Check if user has enough balance
            SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
            float currentBalance = prefs.getFloat("currentBalance", 0);
            if (amount > currentBalance) {
                Toast.makeText(this, R.string.insufficient_funds, Toast.LENGTH_LONG).show();
                return;
            }

            // Create transaction object
            Transaction transaction = new Transaction(amount, category, selectedDate.getTime(), notes);

            // Save transaction to SharedPreferences
            List<Transaction> transactions = loadTransactions();
            transactions.add(0, transaction); // Add new transaction at the beginning of the list
            saveTransactions(transactions);

            // Update balance and spending amount
            float newBalance = currentBalance - (float) amount;
            float spendingAmount = prefs.getFloat("spendingAmount", 0) + (float) amount;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("currentBalance", newBalance);
            editor.putFloat("spendingAmount", spendingAmount);
            editor.apply();

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            amountEditText.setError("Invalid amount format");
        }
    }

    private List<Transaction> loadTransactions() {
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        String transactionsJson = prefs.getString("transactions", "");
        if (!transactionsJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Transaction>>(){}.getType();
            return gson.fromJson(transactionsJson, type);
        }
        return new ArrayList<>();
    }

    private void saveTransactions(List<Transaction> transactions) {
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String transactionsJson = gson.toJson(transactions);
        editor.putString("transactions", transactionsJson);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("amount", amountEditText.getText().toString());
        outState.putInt("categoryPosition", categorySpinner.getSelectedItemPosition());
        outState.putLong("selectedDate", selectedDate.getTimeInMillis());
        outState.putString("notes", notesEditText.getText().toString());
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
