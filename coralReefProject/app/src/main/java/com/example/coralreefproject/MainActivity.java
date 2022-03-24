package com.example.coralreefproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SignInFragment.SListener, HomeFragment.HListener, ForgotPasswordFragment.FListener {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));
        getSupportActionBar().hide();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SignInFragment(), "signin")
                .commit();
    }

    public void hideSupportActionBar() {
        getSupportActionBar().hide();
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
}