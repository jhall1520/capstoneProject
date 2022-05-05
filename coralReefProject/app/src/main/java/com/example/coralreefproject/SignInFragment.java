package com.example.coralreefproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInFragment extends Fragment {
    private FirebaseAuth mAuth;
    SListener sListener;
    EditText email;
    EditText password;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // initialize email and password editText views
        email = view.findViewById(R.id.editTextEmailAddress);
        password = view.findViewById(R.id.editTextPassword);

        // listener on the logIn button
        view.findViewById(R.id.buttonLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the email and password the user entered
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                // if they did not enter an email, display a toast
                if (emailString.equals("")) {
                    Toast.makeText(getActivity(), "Enter an email.", Toast.LENGTH_SHORT).show();
                    return;
                    // if they did not enter a password, display a toast
                } else if (passwordString.equals("")) {
                    Toast.makeText(getActivity(), "Enter a password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get Instance of Firebase's Authentication
                mAuth = FirebaseAuth.getInstance();
                // Check authentication
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // if the user does not have a Display Name
                                    if (user.getDisplayName() == null) {
                                        // Create a screen that makes the user enter they Name
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                        alertDialog.setTitle("Enter User Information");
                                        View inflatedView = LayoutInflater.from(getContext()).inflate(R.layout.user_info_layout, (ViewGroup) getView(), false);
                                        EditText userName = inflatedView.findViewById(R.id.editTextUserName);
                                        alertDialog.setView(inflatedView);

                                        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        });
                                        AlertDialog dialog = alertDialog.create();
                                        dialog.show();
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                String name = userName.getText().toString();
                                                // if user did not enter anything
                                                if (name.equals("")) {
                                                    Toast.makeText(v.getContext(), "Enter your name.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                // Update users name
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name)
                                                        .build();
                                                user.updateProfile(profileUpdates).addOnCompleteListener(task ->
                                                        sListener.goToHomeFragmentFromSignIn());
                                                dialog.dismiss();
                                            }
                                        });
                                    } else {
                                        // Go to the HomeFragment when the user signs in
                                        sListener.goToHomeFragmentFromSignIn();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // if the forgot password button is pressed go to the forgot password fragment
        view.findViewById(R.id.buttonForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sListener.goToForgotPasswordFragment();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).hideSupportActionBar();
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

    public void showToast(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface SListener {
        void goToForgotPasswordFragment();
        void goToHomeFragmentFromSignIn();
    }
}