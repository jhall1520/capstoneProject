package com.example.coralreefproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SignInFragment.SListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SignInFragment(), "signin")
                .commit();
    }

    @Override
    public void goToForgotPasswordFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ForgotPasswordFragment(), "forgot")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSupportFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SupportFragment(), "forgot")
                .addToBackStack(null)
                .commit();
    }
}