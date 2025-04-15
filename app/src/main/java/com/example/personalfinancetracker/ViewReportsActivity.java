
package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewReportsActivity extends AppCompatActivity {

    private static final String TAG = "ViewReportsActivity";
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private String currencySymbol;
    private boolean isGridView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        // Log lifecycle event
        Log.d(TAG, "onCreate() called");

        // Initialize UI components
        transactionsRecyclerView = findViewById(R.id.recycler_transactions);
        ImageButton toggleViewButton = findViewById(R.id.button_toggle_view);

        // Set up navigation buttons
        Button dashboardButton = findViewById(R.id.button_dashboard);
        Button addTransactionButton = findViewById(R.id.button_add_transaction);
        Button viewReportsButton = findViewById(R.id.button_view_reports);

        dashboardButton.setOnClickListener(v -> {
            finish();
        });

        addTransactionButton.setOnClickListener(v -> {
            finish();
            getApplicationContext().startActivity(
                    getPackageManager().getLaunchIntentForPackage(getPackageName())
                            .setClassName(getPackageName(), AddTransactionActivity.class.getName())
            );
        });

        viewReportsButton.setOnClickListener(v -> {
            // Already on view reports, do nothing
        });



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

        // Load transactions
        transactions = loadTransactions();

        // Set up RecyclerView with list view (default)
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this, transactions, currencySymbol);
        transactionsRecyclerView.setAdapter(transactionAdapter);

        // Set up toggle view button
        toggleViewButton.setOnClickListener(v -> {
            isGridView = !isGridView;
            if (isGridView) {
                // Switch to grid view
                transactionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                toggleViewButton.setImageResource(android.R.drawable.ic_dialog_dialer);
            } else {
                // Switch to list view
                transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                toggleViewButton.setImageResource(android.R.drawable.ic_menu_agenda);
            }
        });

        // Restore state if available
        if (savedInstanceState != null) {
            isGridView = savedInstanceState.getBoolean("isGridView", false);
            if (isGridView) {
                transactionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                toggleViewButton.setImageResource(android.R.drawable.ic_dialog_dialer);
            }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isGridView", isGridView);
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

        // Reload transactions in case they've been updated
        transactions = loadTransactions();
        if (transactionAdapter != null) {
            transactionAdapter.updateTransactions(transactions);
        }
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
