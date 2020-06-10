package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    5 April, 2019
*/

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;

/**
 * This fragment contains logic for displaying app usage help content to user
 */
public class HelpFragment extends Fragment {

    Button backButton;

    public HelpFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_help, container, false);
        //ScrollView scrollView=view.findViewById(R.id.ScrollView);


        /*TextView navHead=view.findViewById(R.id.navHeading);
        TextView navTex=view.findViewById(R.id.navText);
        TextView poiHead=view.findViewById(R.id.poiHeading);
        TextView poiTex=view.findViewById(R.id.poiText);
        TextView uhistHead=view.findViewById(R.id.uhistHeading);
        TextView uhistTex=view.findViewById(R.id.uhistText);
        TextView modHead=view.findViewById(R.id.modelHeading);
        TextView modTex=view.findViewById(R.id.modelText);*/
        /*tv.setMovementMethod(new ScrollingMovementMethod());
        navHead.setMovementMethod(new ScrollingMovementMethod());
        navTex.setMovementMethod(new ScrollingMovementMethod());
        poiHead.setMovementMethod(new ScrollingMovementMethod());
        poiTex.setMovementMethod(new ScrollingMovementMethod());
        uhistHead.setMovementMethod(new ScrollingMovementMethod());
        uhistTex.setMovementMethod(new ScrollingMovementMethod());*/

        //tv.setText("usdusd ashd aisd  asjd asd as daj dja sdja sda da sdj asjd ajsd ajd jas doasd oias dia sidj aijsd oiajs doiaj sdoiajsdoaj djasoidj asjd oaisjdoiajdoiajd oaisjf oiasfkah odiakd aoijd oiajd oaijd oiajd oiasjf ahsdoisajd oaisjd oiasjd oiajd oasjdoidjoaisdjoaijdoisjdoaisjdoaisjdoaisjfoijfoaisjfoiasjfoiasjfoiasjfoiasjfoiasjf assaasd as das das d asd asd as da d asd as das dsa d asd asdasd as f  fe g e g g giushdfiuashdiashdoiash doias diuas diusa duiah diuash diuhas diuh asiudh asiudh asiuoaijsf oias foiasj ppppopwoepwopqow pwqoepw qepo epqw epwoqe pwo epwoe pqwoepqwoe pqwe pqwe pqwe pqwoe pqwoe pqwe pqweo pqweo pqweo pqwe pqweo qpwoe pqwoe pqwe pqwoepqwe pqweo pqwe pqwoe pqwoe pqe opqwoepqwoepqow epqwo ep");

        backButton = view.findViewById(R.id.backButtonHelpFragment);
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

}
