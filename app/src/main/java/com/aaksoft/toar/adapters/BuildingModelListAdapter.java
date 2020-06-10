package com.aaksoft.toar.adapters;

/*
    Created By Aasharib
    on
    8 September, 2018
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.fragments.BuildingModelOptionFragment;
import uk.co.appoly.arcorelocation.LocationMarker;

/*
    This class act as adapter for adding building model list
*/

public class BuildingModelListAdapter  extends BaseAdapter implements ListAdapter {

    private List<String> modelList;
    private Context context;
    private BuildingModelOptionFragment fragment;

    public BuildingModelListAdapter(List<String> modelList, Context context, BuildingModelOptionFragment fragment){
        this.modelList = modelList;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
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
            view = layoutInflater.inflate(R.layout.custom_list_view_checkbox, null);
        }

        TextView modelNameTextView = view.findViewById(R.id.textViewModelNameListView);
        modelNameTextView.setText(modelList.get(position));

        CheckBox modelNameCheckbox = view.findViewById(R.id.checkboxModelNameListView);
        modelNameCheckbox.setChecked(((MapsActivity)context).isModelRenderedEnabled.get(position));         //make and array of boolean in MAPS ACTIVITY
        //modelNameCheckbox.setChecked(false);
        modelNameCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ((MapsActivity)context).isModelRenderedEnabled.set(position, true);
                    modelNameCheckbox.setEnabled(false);
                    Timer checkboxTimer = new Timer();
                    checkboxTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ((MapsActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    modelNameCheckbox.setEnabled(true);
                                }
                            });
                        }
                    }, 5000);
                }
                else{
                    //if(((MapsActivity)context).hasSeecsModelFinishedLoading) {
                    if(((MapsActivity)context).hasModelFinishedLoading.get(position)) {
                        ((MapsActivity) context).isModelRenderedEnabled.set(position, false);
                        onUnCheckAdapterAction(position);
                    }
                    else{
                        modelNameCheckbox.setChecked(!isChecked);
                        Toast.makeText(context,"Please wait for a while", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    public void onUnCheckAdapterAction(int position) {
        String tag="";
        if(position == 0){
            tag="seecs";
        }
        else if(position == 1){
            tag="mosque";
        }
        else if(position == 2){
            tag="gate10";
        }
        else if(position == 3){
            tag="gate3";
        }
        else if(position == 4){
            tag="nice";
        }
        if( (((MapsActivity)context).getLocationScene()) != null) {
            ArrayList<LocationMarker> foundMarkerByTag = ((MapsActivity) context).getLocationScene().findMarkersByTag(tag);
            if (foundMarkerByTag != null) {
                for (LocationMarker locationMarker : foundMarkerByTag) {
                    ((MapsActivity) context).removeModel(locationMarker, position);
                }
            }
        }
    }
}