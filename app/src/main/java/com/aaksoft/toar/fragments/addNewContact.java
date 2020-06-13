package com.aaksoft.toar.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class addNewContact extends Fragment


{
    TextView usersInputView;
    Button findContactButton;

    public addNewContact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_contact, container, false);

        usersInputView = view.findViewById(R.id.newContactSearchEditText);
        String userName = (String)usersInputView.getText();
        findContactButton = view.findViewById(R.id.lookforcontactbutton);

        findContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("usernames").child(userName.toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            ((MapsActivity)getActivity()).alertDisplayer("Try Again!", "No such user exists...");
                        }
                        else if(dataSnapshot.getValue() == ((MapsActivity)getActivity()).currentUserPojo.getUsername()){
                            ((MapsActivity)getActivity()).alertDisplayer("Try Again!", "You cannot add yourself into contacts....");
                        }
                        else{
                            String newContactId = (String)dataSnapshot.getValue();
                            ((MapsActivity)getActivity()).alertDisplayer("Found!", newContactId);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ((MapsActivity)getActivity()).alertDisplayer("error", databaseError.getMessage());
                    }
                });

            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
