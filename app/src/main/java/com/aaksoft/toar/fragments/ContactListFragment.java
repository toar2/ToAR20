package com.aaksoft.toar.fragments;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.adapters.contactListAdapter;
import com.aaksoft.toar.firebase.contact;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment {

    private List<contact> userContacts;
    RecyclerView rv;
    FloatingActionButton addNewContactButton;

    public ContactListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container,false);
        rv = view.findViewById(R.id.rv);
        addNewContactButton = (FloatingActionButton) view.findViewById(R.id.goToAddNewContactFragmentButton);
        initializeData();
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        rv.setLayoutManager(llm);
        contactListAdapter adapter = new contactListAdapter(userContacts, getActivity());
        addNewContactButton.setOnClickListener(v ->{

            addNewContact newContactFragment = new addNewContact();
            FragmentManager Fm = getFragmentManager();
            FragmentTransaction ft = Fm.beginTransaction();
            ft.replace(R.id.screen_container, newContactFragment);
            ft.commit();
            removeFragment(this);



        });

        rv.setAdapter(adapter);

        return view;
    }


    private void initializeData(){

        this.userContacts = ((MapsActivity)getActivity()).userContacts;

    }


    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
