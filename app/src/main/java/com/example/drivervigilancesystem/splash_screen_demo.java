
package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splash_screen_demo extends AppCompatActivity {
    public static int SPLASH_TIMER = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_demo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), IP_PAGE.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);
    }
}