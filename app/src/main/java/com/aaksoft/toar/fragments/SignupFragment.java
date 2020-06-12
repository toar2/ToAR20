package com.aaksoft.toar.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.firebase.Users;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


/*
    Fragment for user sign up functionality
 */

public class SignupFragment extends Fragment {

    private FirebaseAuth mAuth;         // this is used to sign up the user
    private FirebaseUser currentUser;
    DatabaseReference usersDatabaseReference;
    DatabaseReference usersImagesDatabaseReference;


    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText usernameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button backButton;
    Button signupButton;
    Button signinButton;
    ProgressBar signupProgressBar;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);



        mAuth = FirebaseAuth.getInstance();         // Fetching an instance of Firebase
        currentUser = mAuth.getCurrentUser();       // Checking status of current user
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users");       // All edits will be made in users node
        usersImagesDatabaseReference = FirebaseDatabase.getInstance().getReference("userImages");

        firstnameEditText = view.findViewById(R.id.firstnameSignup);
        lastnameEditText = view.findViewById(R.id.lastnameSignup);
        usernameEditText = view.findViewById(R.id.usernameSignup);
        userEmailEditText = view.findViewById(R.id.emailSignup);
        passwordEditText = view.findViewById(R.id.passwordSignup);
        confirmPasswordEditText = view.findViewById(R.id.passwordConfirmSignup);
        signupProgressBar = view.findViewById(R.id.progressBarSignup);
        signupButton = view.findViewById(R.id.signupButton);



        signupButton.setOnClickListener(view1 -> {


            String firstname = firstnameEditText.getText().toString().trim();
            String lastname = lastnameEditText.getText().toString().trim();
            String name = firstname + " " + lastname;

            String username = usernameEditText.getText().toString().trim();
            String email = userEmailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();



            if (name.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {

                alertDisplayer("Field Missing", "Please fill the unfilled fields before proceeding");

            } else {


                if (!password.equals(confirmPassword)) {
                    alertDisplayer("Passwords Don't Match", "Please type same password in both fields");


                } else {


                    signupProgressBar.setVisibility(View.VISIBLE);
                    AsyncTask<Void, Void, Void> accountCreation = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            mAuth.createUserWithEmailAndPassword(email, password).
                                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(Task<AuthResult> task) {
                                            currentUser = mAuth.getCurrentUser();
                                            if (task.isSuccessful()) {
                                                ((MapsActivity)getActivity()).clearImageDirectory();            // Delete any preexisting users images
                                                String id = currentUser.getUid();
//                                                String id = usersDatabaseReference.push().getKey();
                                                Users newUser = new Users(name, username, email, id, password);

                                                usersDatabaseReference.child(id).setValue(newUser);
//                                                usersImagesDatabaseReference.child(id).child("FirstImage").setValue("1");
//                                                usersImagesDatabaseReference.child(id).child("SecondImage").setValue("2");
                                                Toast.makeText(getApplicationContext(), "Signup Successful " + currentUser.getUid(), Toast.LENGTH_LONG).show();

                                                ((MapsActivity)getActivity()).beenGreetedOnce = false;
                                                ((MapsActivity)getActivity()).refreshCurrentUser();

                                                ((MapsActivity) getActivity()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        signupProgressBar.setVisibility(View.GONE);
                                                        SigninFragment signinFragment = new SigninFragment();
                                                        FragmentManager fragmentManager = getFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.screen_container, signinFragment);    //add can be used if we want to go to root fragment directly
                                                        fragmentTransaction.addToBackStack(null);
                                                        fragmentTransaction.commit();
                                                        ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;

                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Signup failed!", Toast.LENGTH_LONG).show();
                                                signupProgressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            return null;
                        }
                    };
                    accountCreation.execute();
                }
            }

        });

        signinButton = view.findViewById(R.id.signinButton);
        signinButton.setOnClickListener(view1 -> {
            SigninFragment signinFragment = new SigninFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signinFragment);    //add can be used if we want to go to root fragment directly
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        backButton = view.findViewById(R.id.backButtonSignupFragment);
        backButton.setOnClickListener(view1 -> {
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });


        return view;
    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }


    private void alertDisplayer(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((MapsActivity) getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

}