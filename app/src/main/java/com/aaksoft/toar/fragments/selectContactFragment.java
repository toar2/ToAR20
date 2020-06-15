package com.aaksoft.toar.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.aaksoft.toar.firebase.Memory;
import com.aaksoft.toar.firebase.contact;
import com.aaksoft.toar.localdb.utils.BitmapDataObject;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class selectContactFragment extends Fragment {

    private List<contact> userContacts;
    RecyclerView rv2;
    Button cancelButton;
    Button sendButton;

    // the use of the following is used to construct the memory object
    double lat, lon;
    Bitmap image;
    String description;



    public selectContactFragment(){}


    public static selectContactFragment newInstanceForSendMemories(Bitmap image,  double lat, double lon, String description) {
        selectContactFragment fragment  = new selectContactFragment();

        Bundle memoryArguments = new Bundle();

        BitmapDataObject bitmapDataObject = new BitmapDataObject(image);

        memoryArguments.putSerializable("MemoryImage", bitmapDataObject);
        memoryArguments.putSerializable("lat", lat);
        memoryArguments.putSerializable("lon", lon);
        memoryArguments.putSerializable("description", description);

        fragment.setArguments(memoryArguments);
        return fragment;


    }




    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            BitmapDataObject bitmapDataObject = (BitmapDataObject)getArguments().getSerializable("MemoryImage");
            image = bitmapDataObject.getCurrentImage();
            lat = (double)getArguments().getSerializable("lat");
            lon = (double)getArguments().getSerializable("lon");
            description = (String)getArguments().getSerializable("description");
        }
    }


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
            int noOfContacts = adapter.checkBoxesList.size();
            ArrayList<String> checkedContactIds = new ArrayList<>();


            for(int j = 0; j < noOfContacts; j++){

                if(adapter.checkBoxesList.get(j).isChecked()){                  // check if the check box is checked
                    String contactId = (String)adapter.checkBoxesList.get(j).getTag();                      // get the checkboxes string Id
                    checkedContactIds.add(contactId);                                                       // add the id of the contact to a list
                }

            }
            Toast.makeText(getContext(), checkedContactIds.size() + " checked contacts", Toast.LENGTH_LONG).show();

            Memory newMemory = new Memory();                                                                // new Memory object created
            newMemory.setSenderId(((MapsActivity)getActivity()).getUniqueUserID());
            newMemory.setDescription(description);
            newMemory.setLat(lat);
            newMemory.setLon(lon);
            newMemory.setDate(((MapsActivity)getActivity()).localDatabaseHelper.getDateTime());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            String uniqueImageName = ((MapsActivity)getActivity()).generateUniqueImageName();
//
//            FirebaseDatabase dbref = FirebaseDatabase.getInstance().getReference().child("users").child(((MapsActivity)getActivity()).getUniqueUserID()).child("MemoriesSent").

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("userImages").child(((MapsActivity)getActivity()).getUniqueUserID()).child("MemoriesSent").child(uniqueImageName);

            UploadTask uploadTask = ref.child(uniqueImageName).putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(r -> {
                                    if (r.isSuccessful()) {
                                        Uri uri = r.getResult();
                                        newMemory.setImageUri(uri.toString());
                                        ((MapsActivity)getActivity()).sendMemoriesToContacts(newMemory, checkedContactIds);
//                                        removeFragment(getParentFragment());
                                        cancelButton.performClick();

                                    }
                                    else{
                                        Log.d("Dp url setting failed!", "Failed to retrieve user photo url");
                                    }
                                });
                    } else {
                        Log.w("Dp upload failed!", "Image upload task was not successful.",
                                task.getException());
                    }
                }
            });


        });


        rv2.setAdapter(adapter);

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