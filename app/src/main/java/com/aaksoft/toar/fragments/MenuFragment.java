package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.activities.augmentModels;
import com.google.firebase.auth.FirebaseAuth;

/*
    Handles the Application menu
 */

public class MenuFragment extends Fragment {

    Button backButton;

    Button buildingModelOptionButton;
    Button userContactsButton;
    Button locationHistoryButton;
    Button navigationOptionButton;
    Button helpButton;
    Button uploadImagesToCloudbutton;

    Button signupbutton;
    Button signinbutton;
    Button signoutbutton;


    //Button previewBuildingModelButton;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);



        signinbutton = view.findViewById(R.id.menusigninbutton);
        signoutbutton = view.findViewById(R.id.menusignoutbutton);
        signupbutton = view.findViewById(R.id.menusignupbutton);

        if(((MapsActivity)getActivity()).isUserSignedIn){
            signoutbutton.setVisibility(View.VISIBLE);
            signinbutton.setVisibility(view.GONE);
            signupbutton.setVisibility(view.GONE);
        }
        else{
            // if user is signed in, make sign out button visible only

            signoutbutton.setVisibility(View.GONE);
            signinbutton.setVisibility(view.VISIBLE);
            signupbutton.setVisibility(view.VISIBLE);

        }



        backButton = view.findViewById(R.id.backButtonMenuFragment);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });


        buildingModelOptionButton = view.findViewById(R.id.lookforcontactbutton);
        buildingModelOptionButton.setOnClickListener(view1->{

            Intent intent = new Intent(getActivity(), augmentModels.class);
            intent.putExtra("uniqueUserID" , ((MapsActivity)getActivity()).getUniqueUserID());              // Passing unique userId
            intent.putExtra("latitude", ((MapsActivity)getActivity()).getLatitude());                            // Passing latitude and longitude to
                                                                                                                    // augment models activity
            intent.putExtra("longitude", ((MapsActivity)getActivity()).getLongitude());
            intent.putExtra("userName", ((MapsActivity)getActivity()).getUserName());
            startActivity(intent);
        });

        userContactsButton = view.findViewById(R.id.userContactsMenuButton);
        userContactsButton.setOnClickListener(view1 -> {

            if(((MapsActivity)getActivity()).currentUser == null){

                Toast.makeText(getActivity().getApplicationContext(), "Please login to continue", Toast.LENGTH_LONG).show();
                return;
            }


            ContactListFragment contactListFragment = new ContactListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, contactListFragment);
            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
            removeFragment(this);


        });


        locationHistoryButton = view.findViewById(R.id.buttonLocationHistoryFragment);
        locationHistoryButton.setOnClickListener(view1->{
//            UserLocationHistoryFragment userLocationHistoryFragment = new UserLocationHistoryFragment();
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.screen_container, userLocationHistoryFragment);
//            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
//            removeFragment(this);


            if(((MapsActivity) getActivity()).isUserSignedIn) {
                ((MapsActivity) getActivity()).toggleNavigation();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), "Please login to continue", Toast.LENGTH_LONG).show();

            }



        });
        uploadImagesToCloudbutton = view.findViewById(R.id.syncLocalDbWithCloudButton);
        uploadImagesToCloudbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(((MapsActivity)getActivity()).currentUser != null){

                    ((MapsActivity)getActivity()).synchronizeLocalDbWithCloud();

                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please login to continue", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Can not get user location", Toast.LENGTH_SHORT).show();
                }

            }
        });


//        navigationOptionButton = view.findViewById(R.id.buttonNavigationOptionFragment);
//        navigationOptionButton.setOnClickListener(view1->{
//            NavigationOptionFragment navigationOptionFragment = NavigationOptionFragment.newInstance(((MapsActivity)getActivity()).getCurAppMode());
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.screen_container, navigationOptionFragment);
//            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
//            removeFragment(this);
//        });

        helpButton = view.findViewById(R.id.buttonHelpFragment);
        helpButton.setOnClickListener(view1->{
            HelpFragment helpFragment = new HelpFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, helpFragment);
            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
            removeFragment(this);
        });


        signoutbutton.setOnClickListener(view1->{
            ((MapsActivity)getActivity()).refreshCurrentUser();

            ((MapsActivity)getActivity()).clearImageDirectory();

            FirebaseAuth.getInstance().signOut();
            removeFragment(this);
//            signinbutton.setVisibility(View.VISIBLE);
//            signupbutton.setVisibility(View.VISIBLE);
//            signoutbutton.setVisibility(View.GONE);
//            usernameTextView.setText("You are login as: Local Guest");
//            usernameTextView.setVisibility(View.GONE);



        });

        signupbutton.setOnClickListener(view1->{
            SignupFragment signupFragment = new SignupFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signupFragment);
            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
            removeFragment(this);}
            );

        signinbutton.setOnClickListener(view1->{
            SigninFragment signinFragment = new SigninFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signinFragment);
            fragmentTransaction.commit();
//            fragmentTransaction.addToBackStack(null);
            removeFragment(this);});

        return view;




    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }


}
