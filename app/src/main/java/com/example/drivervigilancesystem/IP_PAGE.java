package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IP_PAGE extends AppCompatActivity {
    EditText e;
    Button b;
    SharedPreferences sh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_page);
        e = findViewById(R.id.editTextTextPersonName2);
        b = findViewById(R.id.button2);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e.setText(sh.getString("ip", ""));


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = e.getText().toString();
                String url1 = "http://" + ip + ":5000/";
                SharedPreferences.Editor ed = sh.edit();
                ed.putString("ip", ip);
                ed.putString("url", url1);
                ed.commit();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }
        });
    }
        private void createFloatView() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkDrawOverlayPermission();
            }
        }

        public void checkDrawOverlayPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 111);
                }
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 111) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {

                    }
                }
            }
        }

        public void settingPermission() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 200);

                }
            }
    }
}