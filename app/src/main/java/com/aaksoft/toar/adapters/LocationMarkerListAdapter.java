package com.aaksoft.toar.adapters;

/*
    Created By Aasharib
    on
    2 August, 2018
 */

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.api.google.Information.Places;
import com.aaksoft.toar.fragments.LocationMarkerDetailsDescriptionFragment;
import com.aaksoft.toar.fragments.LocationMarkerListFragment;

/*
    This class act as adapter for location marker list returned from Google Place API
*/

public class LocationMarkerListAdapter extends BaseAdapter implements ListAdapter {

    private List<Places> placesList;
    private Context context;
    private LocationMarkerListFragment fragment;

    public LocationMarkerListAdapter(List<Places> placesList, Context context, LocationMarkerListFragment fragment){
        this.placesList = placesList;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return placesList.size();
    }

    @Override
    public Object getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            //LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(R.layout.custom_list_view_layout, null);
        }
        TextView nameListFragmentTextView = view.findViewById(R.id.nameListFragmentTextView);
        nameListFragmentTextView.setText(placesList.get(position).getName());

        TextView infoListFragmentTextView = view.findViewById(R.id.infoListFragmentTextView);
        infoListFragmentTextView.setText("Rating: " + placesList.get(position).getRating());

        Button showDetailsFragmentListButton = view.findViewById(R.id.showDetailsFragmentListButton);
        showDetailsFragmentListButton.setOnClickListener(view1->{
            LocationMarkerDetailsDescriptionFragment locationMarkerDetailsDescriptionFragment = LocationMarkerDetailsDescriptionFragment.newInstance(placesList.get(position));
            FragmentManager fragmentManager = fragment.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.add(R.id.locationDetailContainerFragment, locationMarkerDetailsDescriptionFragment);
            fragmentTransaction.replace(R.id.screen_container, locationMarkerDetailsDescriptionFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });

        Button navigateListFragmentButton = view.findViewById(R.id.navigateListFragmentButton);
        navigateListFragmentButton.setOnClickListener(view1->{
            ((MapsActivity)context).setDestinationSelected(true);
            ((MapsActivity)context).routeNavigate(placesList.get(position).getLng(),placesList.get(position).getLat());
            ((MapsActivity)context).hideListViewButton();
            fragment.removeFragement(fragment);
            //fragment.getFragmentManager().popBackStackImmediate();
            //((MapsActivity)context).onBackPressed();
        });
        return view;
    }
}
