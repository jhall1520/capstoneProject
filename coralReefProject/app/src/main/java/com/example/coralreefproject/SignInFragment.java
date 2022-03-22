package com.example.coralreefproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SignInFragment extends Fragment {

    SListener sListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        view.findViewById(R.id.buttonLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: need to check database to see if the credentials are correct
                sListener.goToHomeFragment();
            }
        });

        view.findViewById(R.id.buttonForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sListener.goToForgotPasswordFragment();
            }
        });

        view.findViewById(R.id.buttonSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sListener.goToSupportFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SListener) {
            sListener = (SListener) context;
        } else {
            throw new IllegalStateException("MainActivity needs to implement SListener from SignInFragment.java");
        }
    }

    public interface SListener {
        void goToForgotPasswordFragment();
        void goToSupportFragment();
        void goToHomeFragment();
    }
}