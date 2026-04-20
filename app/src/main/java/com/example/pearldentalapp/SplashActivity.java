package com.example.pearldentalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find Views
        ImageView imgLogo = findViewById(R.id.logo_splash);
        TextView tvAppName = findViewById(R.id.Title_splash);

        // Load Animations
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Start Animations
        imgLogo.startAnimation(logoAnim);
        tvAppName.startAnimation(textAnim);

        // Move to login screen after animation completes
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, loginactivity.class);
            startActivity(intent);
            finish();
        }, 2000); // Wait for animations to complete before switching
    }
}
