package com.example.coralreefproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SignInFragment.SListener, HomeFragment.HListener,
        ForgotPasswordFragment.FListener, CoralDatabaseFragment.EntryAdapter.CListener, LocationListener {

    private LocationManager locationManager;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));
//        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
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
//        getSupportActionBar().setTitle("New Data Entry");
//        getSupportActionBar().show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("This device does not have permission to get GPS coordinates.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, DataEntryFragment.newInstance(location), "entry")
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage("This device does not have permission to get GPS coordinates.")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

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
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}