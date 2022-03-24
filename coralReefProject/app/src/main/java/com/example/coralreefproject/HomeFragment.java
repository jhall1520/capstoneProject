package com.example.coralreefproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class HomeFragment extends Fragment {

    HListener hListener;

    private static final String ARG_USER = "user";

    private FirebaseUser user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView welcome = view.findViewById(R.id.textViewWelcomeUser);
        String userName = user.getDisplayName();
        if (userName.contains(" ")) {
            userName = userName.split(" ")[0];
        }
        welcome.setText("Welcome " + userName + "!");

        view.findViewById(R.id.buttonNewEntry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hListener.goToDataEntryFragment();
            }
        });

        view.findViewById(R.id.buttonDatabase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hListener.goToCoralDatabaseFragment();
            }
        });

        view.findViewById(R.id.buttonDataAnalysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hListener.goToDataAnalysisFragment();
            }
        });

        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                hListener.logOut();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof HListener) {
            hListener = (HListener) context;
        } else {
            throw new IllegalStateException("MainActivity needs to implement HListener from HomeFragment.java");
        }
    }

    public interface HListener {
        void goToCoralDatabaseFragment();
        void goToDataEntryFragment();
        void goToDataAnalysisFragment();
        void logOut();
    }
}