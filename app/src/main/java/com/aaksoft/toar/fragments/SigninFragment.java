package com.aaksoft.toar.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class SigninFragment extends Fragment {

    EditText emailSignin;
    EditText passwordSignin;

    Button signinButton;
    Button signupButton;
    Button backButton;
    ProgressBar signinProgressBar;

    private FirebaseAuth mAuth;         // this is used to sign up the user
    private FirebaseUser currentUser;
    DatabaseReference usersDatabaseReference;

    public SigninFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        emailSignin = view.findViewById(R.id.emailSignin);
        passwordSignin = view.findViewById(R.id.passwordSignin);

        signinProgressBar = view.findViewById(R.id.progressBarSignin);

        signinButton = view.findViewById(R.id.signinButtonSigninFragment);
        signinButton.setOnClickListener(view1->{


            signinProgressBar.setVisibility(View.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            String email = emailSignin.getText().toString().trim();
            String password = passwordSignin.getText().toString().trim();

            if(!email.equals("") && !password.equals("")) {      // if the username and password are not null

                AsyncTask<Void, Void, Void> accountSignin = new AsyncTask<Void, Void, Void>(){
                    @Override


                    protected Void doInBackground(Void... params) {

                        mAuth.signInWithEmailAndPassword(email, password).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {

                                signinProgressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    ((MapsActivity)getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((MapsActivity)getActivity()).clearImageDirectory();
                                            signinProgressBar.setVisibility(View.GONE);
                                            ((MapsActivity)getActivity()).beenGreetedOnce = false;
                                            ((MapsActivity)getActivity()).refreshCurrentUser();

                                            Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Signin failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        return null;
                    }

                };
                accountSignin.execute();

            }
            else{
                Toast.makeText(getApplicationContext(), "Please fill both fields", Toast.LENGTH_LONG);
            }
        });

        signupButton = view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(view1->{
            SignupFragment signupFragment = new SignupFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signupFragment);    //add can be used if we want to go to root fragment directly
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        backButton = view.findViewById(R.id.backButtonSigninFragment);
        backButton.setOnClickListener(view1->{
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

    public void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder((MapsActivity)getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                ok.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
        });
        ok.show();
    }

    private int updateUI(int a){
        return 0;
    }
}

