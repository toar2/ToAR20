package com.aaksoft.toar.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaksoft.toar.R;
import com.aaksoft.toar.firebase.contact;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class contactListAdapter extends RecyclerView.Adapter<contactListAdapter.PersonViewHolder>{
    List<contact> userContactList;
    Context con;
    boolean selectContactsScreen = false;
    public List <CheckBox> checkBoxesList;
//    public ArrayList<ImageButton> imageButtonsList;



    public contactListAdapter(List<contact> persons, Context c){
        this.userContactList = persons;
        con = c;
        checkBoxesList = new ArrayList<CheckBox>();

    }
    public contactListAdapter(List<contact> persons, Context c, boolean selectContactsScreen){
        this.userContactList = persons;
        con = c;
        this.selectContactsScreen = selectContactsScreen;
        checkBoxesList = new ArrayList<CheckBox>();

    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_viewholder, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override

    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {



        personViewHolder.contactName.setText(userContactList.get(i).name);
        personViewHolder.contactUserName.setText(userContactList.get(i).username);
        personViewHolder.messageContactButton.setTag(userContactList.get(i).id);
        personViewHolder.selectContactCheckBox.setTag(userContactList.get(i).id);

        if(selectContactsScreen){
            personViewHolder.messageContactButton.setVisibility(View.GONE);
            personViewHolder.selectContactCheckBox.setVisibility(View.VISIBLE);
            checkBoxesList.add(personViewHolder.selectContactCheckBox);
        }


        if(userContactList.get(i).photoURL != null){
            Glide.with(con)
                    .load(userContactList.get(i).photoURL)
                    .into(personViewHolder.contactImg);
        }




    }

    @Override
    public int getItemCount() {
       return this.userContactList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView contactName;
        TextView contactUserName;
        ImageButton messageContactButton;
        CheckBox selectContactCheckBox;
        ImageView contactImg;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.contactCardView);
            contactName = (TextView)itemView.findViewById(R.id.contact_name);
            contactUserName = (TextView)itemView.findViewById(R.id.contact_username);
            messageContactButton = (ImageButton)itemView.findViewById(R.id.messageContactButton);
            contactImg = (ImageView)itemView.findViewById(R.id.contactCardDpImageView);
            selectContactCheckBox = (CheckBox)itemView.findViewById(R.id.selectContactCheckBox);
        }
    }

}