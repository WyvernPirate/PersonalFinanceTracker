package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Log lifecycle event
        Log.d(TAG, "onCreate() called");

        // Animate the piggy bank
        ImageView piggyBank = findViewById(R.id.splash_piggy_bank);
        Animation bounce = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        piggyBank.startAnimation(bounce);

        // Delayed transition to next activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check if first time setup is required
            SharedPreferences prefs = getSharedPreferences("PersonalFinanceTracker", MODE_PRIVATE);
            boolean isFirstTime = !prefs.contains("userName");

            Intent intent;
            if (isFirstTime) {
                intent = new Intent(SplashActivity.this, FirstTimeSetupActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
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
