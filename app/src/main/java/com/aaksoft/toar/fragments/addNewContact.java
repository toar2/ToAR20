package com.aaksoft.toar.fragments;

import android.content.Context;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.firebase.contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addNewContact extends Fragment
{
    EditText usersInputView;
    Button findContactButton;


    public addNewContact() {
        // Required empty public constructor
    }


    public void onBackPressed(){

        removeFragment(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_contact, container, false);

        usersInputView = view.findViewById(R.id.newContactSearchEditText);

        findContactButton = view.findViewById(R.id.lookforcontactbutton);

        findContactButton.setOnClickListener(view1 -> {


                String userName = usersInputView.getText().toString();
                String userNameUpperCase = userName.toUpperCase();

                if(userName.equals("")){                // if user has left field blank
                    ((MapsActivity)getActivity()).alertDisplayer("Error!","Please fill field before continuing...");

                }
                else{

                    FirebaseDatabase.getInstance().getReference().child("userNames").child(userNameUpperCase).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if( dataSnapshot.exists()){

                                FirebaseDatabase.getInstance().getReference().child("users").child(dataSnapshot.getValue().toString()).child("profileInformation").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                        if(dataSnapshot2.exists()){

                                            // check if user id is not same as own

                                            contact latestContact = dataSnapshot2.getValue(contact.class);
//                                            ((MapsActivity)getActivity()).alertDisplayer("Try Again",latestContact.id);

                                            if(latestContact.id.equals(((MapsActivity)getActivity()).currentUserPojo.getId())){
                                                ((MapsActivity)getActivity()).alertDisplayer("Try Again", "You cannot add yourself as a contact...");
                                            }

                                            else{

                                                // check if the user already exists in user contacts
                                                boolean alreadyExists = false;
                                                for (int i = 0 ; i < ((MapsActivity)getActivity()).userContacts.size(); i++){

                                                    if(latestContact.id.equals(((MapsActivity)getActivity()).userContacts.get(i).id)){
                                                        ((MapsActivity)getActivity()).alertDisplayer("Already exits!", "This user is already in your contacts...");
                                                        alreadyExists = true;
                                                        break;
                                                    }
                                                }

                                                if(!alreadyExists){
                                                    ((MapsActivity)getActivity()).userContacts.add(latestContact);
                                                    ((MapsActivity)getActivity()).alertDisplayer("Contact Added!",latestContact.name + " has been added to your contacts!");


                                                    // upload the contact online

                                                    FirebaseDatabase.getInstance().getReference().child("users").child(((MapsActivity)getActivity()).currentUserPojo.getId()).child("contacts").child(latestContact.id).setValue(latestContact);






                                                }

                                            }


                                        }
                                        else{
                                            ((MapsActivity)getActivity()).alertDisplayer("Try Again","Error retrieving User information");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        ((MapsActivity)getActivity()).alertDisplayer("Try Again",databaseError.getMessage());



                                    }
                                });







                            }
                            else{
                                ((MapsActivity)getActivity()).alertDisplayer("Try Again","This user name does not exist...");
                                return;
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            ((MapsActivity)getActivity()).alertDisplayer("Error", databaseError.getMessage());

                        }
                    });



                }



        });

        return view;
    }




    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }


}
