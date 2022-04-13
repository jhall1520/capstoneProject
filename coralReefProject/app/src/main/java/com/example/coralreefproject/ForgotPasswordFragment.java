package com.example.coralreefproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordFragment extends Fragment {

    FListener fListener;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        EditText email = view.findViewById(R.id.editTextTextEmailAddress2);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        view.findViewById(R.id.buttonResetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();

                if (emailString.equals("")) {
                    Toast.makeText(getActivity(), "Enter your email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                    mAuth.sendPasswordResetEmail(emailString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                            .setTitle("Success")
                                            .setMessage("An email was sent to reset your password!")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    fListener.goToSignInFromForgotPassword();
                                                }
                                            }).show();
                                } else {
                                    String exception = task.getException().toString();
                                    Toast.makeText(getActivity(), exception.split(": ")[1], Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FListener) {
            fListener = (FListener) context;
        } else {
            throw new IllegalStateException("MainActivity needs to implement FListener from ForgotPasswordFragment.java");
        }
    }

    public interface FListener {
        void goToSignInFromForgotPassword();
    }
}