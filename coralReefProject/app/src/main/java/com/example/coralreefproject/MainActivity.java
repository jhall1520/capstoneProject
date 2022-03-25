package com.example.coralreefproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SignInFragment.SListener, HomeFragment.HListener,
        ForgotPasswordFragment.FListener, CoralDatabaseFragment.EntryAdapter.CListener {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));
        getSupportActionBar().hide();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment(), "home")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SignInFragment(), "signIn")
                    .commit();
        }
    }

    public void hideSupportActionBar() {
        getSupportActionBar().hide();
    }

    public void showSupportActionBar() {
        getSupportActionBar().show();
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

    @Override
    public void goToHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment(), "home")
                .commit();
    }

    @Override
    public void goToCoralDatabaseFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new CoralDatabaseFragment(), "database")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToDataEntryFragment() {
        //getSupportActionBar().setTitle("New Data Entry");
        //getSupportActionBar().show();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DataEntryFragment(), "entry")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToDataAnalysisFragment() {

    }

    @Override
    public void logOut() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignInFragment(), "signIn")
                .commit();
    }

    @Override
    public void gotToSignInFromForgotPassword() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToViewDataFragment(CoralEntry entry) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ViewDataFragment.newInstance(entry), "view")
                .addToBackStack(null)
                .commit();
    }
}