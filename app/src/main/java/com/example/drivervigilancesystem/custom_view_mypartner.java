package com.example.drivervigilancesystem;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.drivervigilancesystem.databinding.ActivityCustomViewMypartnerBinding;

public class custom_view_mypartner extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCustomViewMypartnerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomViewMypartnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

}