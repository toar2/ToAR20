package com.aaksoft.toar.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import com.aaksoft.toar.firebase.getJointNode;
import com.aaksoft.toar.firebase.message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.NoSuchAlgorithmException;


/*
    Fragment for user sign up functionality
 */

public class SignupFragment extends Fragment {

    private FirebaseAuth mAuth;         // this is used to sign up the user
    private FirebaseUser currentUser;
    DatabaseReference usersDatabaseReference;
    private static final int REQUEST_IMAGE = 2;
    boolean shouldUploadUserDp = false;                                 // indicates whether app should upload a dp for the user


    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText usernameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button backButton;
    Button signupButton;
    Button signinButton;
    Button uploadDp;
    Uri uri;
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
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference();       // All edits will be made in users node

        firstnameEditText = view.findViewById(R.id.firstnameSignup);
        lastnameEditText = view.findViewById(R.id.lastnameSignup);
        usernameEditText = view.findViewById(R.id.usernameSignup);
        userEmailEditText = view.findViewById(R.id.emailSignup);
        passwordEditText = view.findViewById(R.id.passwordSignup);
        confirmPasswordEditText = view.findViewById(R.id.passwordConfirmSignup);
        signupProgressBar = view.findViewById(R.id.progressBarSignup);
        signupButton = view.findViewById(R.id.signupButton);
        uploadDp = view.findViewById(R.id.ChooseDpButton);

        uploadDp.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
        });



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

                    FirebaseDatabase.getInstance().getReference("userNames").child(username.toUpperCase()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                alertDisplayer("Error", "The username you have entered is already in use. ");
                            }
                            else{
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
                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(name).build();

                                                            currentUser.updateProfile(profileUpdates);
                                                            String id = currentUser.getUid();
                                                            String hashedPassword;

                                                            try{
                                                                hashedPassword = getJointNode.sha256(password + lastname);                                     // getting the hash of the password
                                                                // the user added
                                                                Users newUser = new Users(name, username, email, id, hashedPassword);               // uploading the user pojo

                                                                usersDatabaseReference.child("userNames").child(username.toUpperCase()).setValue(id);
                                                                usersDatabaseReference.child("users").child(id).child("profileInformation").setValue(newUser);
                                                                // now here upload new found user's data to storage
                                                                if(shouldUploadUserDp){


                                                                    FirebaseStorage.getInstance().getReference().child("profilePictures").child(id)
                                                                            .putFile(uri).addOnCompleteListener(getActivity(),
                                                                            new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        task.getResult().getMetadata().getReference().getDownloadUrl()
                                                                                                .addOnCompleteListener(r -> {
                                                                                                    if (r.isSuccessful()) {

                                                                                                        // setting the user's profile picture id url



                                                                                                        usersDatabaseReference.child("users").child(id).child("profileInformation").child("photoURL").setValue(r.getResult().toString());
                                                                                                        UserProfileChangeRequest profileUpdates2 = new UserProfileChangeRequest.Builder()
                                                                                                                .setPhotoUri(r.getResult()).build();
                                                                                                        currentUser.updateProfile(profileUpdates2);

                                                                                                    }
                                                                                                    else{
                                                                                                        Log.d("Dp url setting failed!", "Failed to retrieve user photo url");

                                                                                                    }

                                                                                                });




                                                                                    } else {
                                                                                        Log.w("Dp upload failed!", "Image upload task was not successful.",
                                                                                                task.getException());
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                                else{

                                                                }





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
                                                            }
                                                            catch(NoSuchAlgorithmException e){
                                                                e.printStackTrace();
                                                            }



                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Signup failed!", Toast.LENGTH_LONG).show();
                                                            alertDisplayer("SignUp Failed!", task.getException().getMessage());
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

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            alertDisplayer("Error", databaseError.getMessage());

                        }
                    });



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


    @Override


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("SignUpActivityRequestCode", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    Log.d("SignUpActivityDpUri", "Uri: " + uri.toString());

                    shouldUploadUserDp = true;
                    this.uploadDp.setText("Image Retrieved!");


                }
            }
        }
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