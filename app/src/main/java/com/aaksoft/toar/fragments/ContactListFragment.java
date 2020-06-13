package com.aaksoft.toar.fragments;

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

    public ContactListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container,false);
        rv = view.findViewById(R.id.rv);

        initializeData();
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        rv.setLayoutManager(llm);
        contactListAdapter adapter = new contactListAdapter(userContacts);

        rv.setAdapter(adapter);

        return view;
    }


    private void initializeData(){
        userContacts = new ArrayList<>();
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
        userContacts.add(new contact("Emma Wilson", "Emma69____", "Id1"));
        userContacts.add(new contact("Lavery Maiss", "Lavander232", "Id2"));
        userContacts.add(new contact("Lillie Watts", "LillyzHoe", "Id3"));
    }


    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
