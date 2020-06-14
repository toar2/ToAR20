package com.aaksoft.toar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.adapters.contactListAdapter;
import com.aaksoft.toar.firebase.contact;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class selectContactFragment extends Fragment {

    private List<contact> userContacts;
    RecyclerView rv2;
    Button cancelButton;
    Button sendButton;

    public selectContactFragment(){}

    @Nullable
    @Override

    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, Bundle savedInstanceState){

        View view = layoutInflater.inflate(R.layout.fragment_selectusercontacts, container, false);
        rv2 = view.findViewById(R.id.rv2);
        cancelButton = view.findViewById(R.id.cancelButton_SelectUsersFragment);
        sendButton = view.findViewById(R.id.sendButton_selectUsersFragment);
        initializeData();
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        rv2.setLayoutManager(llm);
        contactListAdapter adapter = new contactListAdapter(userContacts, getActivity(), true);

        cancelButton.setOnClickListener(view1 -> {
            removeFragment(this);
        });

        sendButton.setOnClickListener(view1 ->{


        Toast.makeText(getContext(), adapter.checkBoxesList.size() + " is the number of checkboxes", Toast.LENGTH_LONG).show();

        });




        return view;

    }



    private void initializeData(){
        this.userContacts = ((MapsActivity)getActivity()).userContacts;
    }
    protected void removeFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
