
package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView welcomeTextView;
    private TextView currentBalanceTextView;
    private TextView spendingAmountTextView;
    private RecyclerView recentTransactionsRecyclerView;
    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private String userName;
    private float currentBalance;
    private float spendingAmount;
    private String currency;
    private String currencySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log lifecycle event
        Log.d(TAG, "onCreate() called");

        // Initialize UI components
        welcomeTextView = findViewById(R.id.text_welcome);
        currentBalanceTextView = findViewById(R.id.text_current_balance);
        spendingAmountTextView = findViewById(R.id.text_spending_amount);
        recentTransactionsRecyclerView = findViewById(R.id.recycler_recent_transactions);

        // Set up navigation buttons
        Button dashboardButton = findViewById(R.id.button_dashboard);
        Button addTransactionButton = findViewById(R.id.button_add_transaction);
        Button viewReportsButton = findViewById(R.id.button_view_reports);

        dashboardButton.setOnClickListener(v -> {
            // Already on dashboard, do nothing
        });

        addTransactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        viewReportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewReportsActivity.class);
            startActivity(intent);
        });

        // Load user data from SharedPreferences
        loadUserData();

        // Set up RecyclerView
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactions = loadTransactions();
        transactionAdapter = new TransactionAdapter(this, transactions, currencySymbol);
        recentTransactionsRecyclerView.setAdapter(transactionAdapter);

        // Update UI
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        // Reload user data and transactions in case they've been updated
        loadUserData();
        transactions = loadTransactions();
        if (transactionAdapter != null) {
            transactionAdapter.updateTransactions(transactions);
        }
        updateUI();
        checkBalance();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        userName = prefs.getString("userName", "");
        currentBalance = prefs.getFloat("currentBalance", 0);
        spendingAmount = prefs.getFloat("spendingAmount", 0);
        currency = prefs.getString("currency", "USD ($)");

        // Extract currency symbol
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

    private void updateUI() {
        welcomeTextView.setText("Welcome, " + userName + "!");
        currentBalanceTextView.setText(currencySymbol + String.format("%.2f", currentBalance));
        spendingAmountTextView.setText(currencySymbol + String.format("%.2f", spendingAmount));
    }

    private void checkBalance() {
        if (currentBalance < 4) {
            // Critical low balance, force restart
            showForcedRestartDialog();
        } else if (currentBalance < 10) {
            // Low balance warning
            showLowBalanceWarningDialog();
        }
    }

    private void showLowBalanceWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Low Balance Warning");
        builder.setMessage(getString(R.string.low_balance_warning));
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> restartApp());
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        builder.setCancelable(true);
        builder.show();
    }

    private void showForcedRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Critical Low Balance");
        builder.setMessage(getString(R.string.low_balance_warning));
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> restartApp());
        builder.setCancelable(false);
        builder.show();
    }

    private void restartApp() {
        SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save only the user's name
        String userName = prefs.getString("userName", "");

        // Clear all other preferences
        editor.clear();
        editor.putString("userName", userName);
        editor.apply();

        // Navigate to FirstTimeSetupActivity
        Intent intent = new Intent(this, FirstTimeSetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_add_transaction) {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_view_reports) {
            Intent intent = new Intent(this, ViewReportsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_settings) {
            // Settings functionality would be implemented here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
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
