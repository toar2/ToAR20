package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    20 March, 2019
 */

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.localdb.utils.AppSetting;


import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.






import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * Contains the logic for displaying user location based history after the user signin and signup
 * Allows user to enable disable location history
 */
public class UserLocationHistoryFragment extends Fragment {

    CheckBox enableLocationHistoryCheckBox;
    public static final int SELECT_IMAGE = 100;
    Button backButton;

    Button signinButton;
    Button signupButton;
    Button signoutButton;
    TextView usernameTextView;

    SeekBar seekBar;
    int radius;

    public UserLocationHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_location_history, container, false);

        backButton = view.findViewById(R.id.backButtonUserHistoryFragment);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            ((MapsActivity)getActivity()).removeMapMarkerOfParticularClass("Loc_History_Marker");
            removeFragment(this);
            //((MapsActivity)getActivity()).switchToCameraMode();
        });

        enableLocationHistoryCheckBox = view.findViewById(R.id.checkboxEnableLocationHistory);
        enableLocationHistoryCheckBox.setChecked(((MapsActivity)getActivity()).getIsLocationHistoryEnabled());
        enableLocationHistoryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ((MapsActivity)getActivity()).setIsLocationHistoryEnabled(true);
//                    ((MapsActivity)getActivity()).takePicButton.setVisibility(View.VISIBLE);
                    ((MapsActivity)getActivity()).loadImageInformation();
                    ((MapsActivity)getActivity()).makeRenderablePicture();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {

                            AppSetting locationHistoryRenderableRangeSetting = new AppSetting("loc_hist_enabled","true");
                            locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);


                        }
                    });
                    ((MapsActivity)getActivity()).userSignStatusButton.setVisibility(View.VISIBLE);
                    //((MapsActivity)getActivity()).switchToCameraMode();
                }
                else{
                    ((MapsActivity)getActivity()).setIsLocationHistoryEnabled(false);
//                    ((MapsActivity)getActivity()).takePicButton.setVisibility(View.GONE);


                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppSetting locationHistoryRenderableRangeSetting = new AppSetting("loc_hist_enabled","false");
                            locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);
                        }
                    });
                    ((MapsActivity)getActivity()).userSignStatusButton.setVisibility(View.GONE);
                }
            }
        });

        TextView radiusTextView = view.findViewById(R.id.radiusImageFindTextViewULHFragment);
        radiusTextView.setText(Integer.toString(((MapsActivity) getActivity()).getImageRenderableDistance())+"m");

        seekBar = view.findViewById(R.id.seekBarImageFindRadius);
        seekBar.setProgress(((MapsActivity)getActivity()).getImageRenderableDistance());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = Integer.valueOf(progress);
                ((MapsActivity)getActivity()).setImageRenderableDistance(radius);
                radiusTextView.setText(radius+"m");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ((MapsActivity)getActivity()).loadImageInformation();
                        AppSetting locationHistoryRenderableRangeSetting = new AppSetting("loc_hist_range",Integer.toString(radius));
                        locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);
                    }
                });
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        signinButton = view.findViewById(R.id.locationHistorySigninButton);
        signinButton.setOnClickListener(view1->{
            SigninFragment signinFragment = new SigninFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signinFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
            //signinButton.setVisibility(View.GONE);
            //signoutButton.setVisibility(View.VISIBLE);
            removeFragment(this);
        });

        signupButton = view.findViewById(R.id.locationHistorySignupButton);
        signupButton.setOnClickListener(view1->{
            SignupFragment signupFragment = new SignupFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, signupFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
            removeFragment(this);
        });

        signoutButton = view.findViewById(R.id.signoutButtonUserLocationHistoryFragment);
        signoutButton.setOnClickListener(view1->{

            ((MapsActivity)getActivity()).clearImageDirectory();
            FirebaseAuth.getInstance().signOut();
            signinButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);
            signoutButton.setVisibility(View.GONE);
            usernameTextView.setText("You are login as: Local Guest");
            usernameTextView.setVisibility(View.GONE);

            ((MapsActivity)getActivity()).refreshCurrentUser();
        });


        usernameTextView = view.findViewById(R.id.loginUsernameTextview);

        if(((MapsActivity)getActivity()).getIsLocationHistoryEnabled()){
            ((MapsActivity)getActivity()).takePicButton.setVisibility(View.VISIBLE);
            ((MapsActivity)getActivity()).loadImageInformation();
            ((MapsActivity)getActivity()).makeRenderablePicture();
        }

        if(((MapsActivity)getActivity()).isUserSignedIn){
            signinButton.setVisibility(View.GONE);                      // Remove Sign up option if user is signed in
            signupButton.setVisibility(View.GONE);
            signoutButton.setVisibility(View.VISIBLE);
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText("You are signed in as: " + ((MapsActivity)getActivity()).getUserName());
        }
        else{
            signinButton.setVisibility(View.VISIBLE);
            signoutButton.setVisibility(View.GONE);
            usernameTextView.setVisibility(View.GONE);
        }

        return view;
    }



    /*public void SelectImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }*/

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
