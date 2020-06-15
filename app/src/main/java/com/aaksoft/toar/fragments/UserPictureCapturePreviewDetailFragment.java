package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    26 January, 2019
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.activities.augmentModels;
import com.aaksoft.toar.localdb.utils.BitmapDataObject;

/**
     Contains logic for saving the image along with its meta data
    It receives image from maps activity
 */
public class UserPictureCapturePreviewDetailFragment extends Fragment {

    private ImageView previewImageView;
    private EditText commentEditText;
    private Button cancelSavePictureButton;
    private Button savePictureButton;
    private Button sharePictureButton;
    private Button sendPictureAsMemoryButton;

    private boolean shareImage = false;
    private Bitmap image;
    private String uniqueFileName;
    private String imagePath;
    private double lat;
    private double lng;
    private String description;
    private String owner_id;
    private String owner_name;



    public UserPictureCapturePreviewDetailFragment() {
        // Required empty public constructor
    }

    public static UserPictureCapturePreviewDetailFragment newInstance(Bitmap image,String uniqueFileName, String imagePath, double lat, double lng, String ownerId, String owner_name){
        UserPictureCapturePreviewDetailFragment fragment = new UserPictureCapturePreviewDetailFragment();
        Bundle args = new Bundle();
        BitmapDataObject bitmapDataObject = new BitmapDataObject(image);
        args.putSerializable("image", bitmapDataObject);
        args.putSerializable("uniquefilename", uniqueFileName);
        args.putSerializable("imagepath", imagePath);
        args.putSerializable("lat", lat);
        args.putSerializable("lng", lng);
        args.putSerializable("owner_id",ownerId);
        args.putSerializable("owner_name",owner_name);
        args.putSerializable("share_image", false);

        fragment.setArguments(args);
        return fragment;
    }

    public static UserPictureCapturePreviewDetailFragment newInstance(Bitmap image,String uniqueFileName, String imagePath, double lat, double lng, String ownerId, String owner_name, boolean share){
        UserPictureCapturePreviewDetailFragment fragment = new UserPictureCapturePreviewDetailFragment();
        Bundle args = new Bundle();
        BitmapDataObject bitmapDataObject = new BitmapDataObject(image);
        args.putSerializable("image", bitmapDataObject);
        args.putSerializable("uniquefilename", uniqueFileName);
        args.putSerializable("imagepath", imagePath);
        args.putSerializable("lat", lat);
        args.putSerializable("lng", lng);
        args.putSerializable("owner_id",ownerId);
        args.putSerializable("owner_name",owner_name);
        args.putSerializable("share_image", share);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            BitmapDataObject bitmapDataObject = (BitmapDataObject) getArguments().getSerializable("image");
            image = bitmapDataObject.getCurrentImage();
            uniqueFileName = (String) getArguments().getSerializable("uniquefilename");
            imagePath = (String) getArguments().getSerializable("imagepath");
            lat = (double) getArguments().getSerializable("lat");
            lng = (double) getArguments().getSerializable("lng");
            owner_id = (String) getArguments().getSerializable("owner_id");
            owner_name = (String) getArguments().getSerializable("owner_name");
            shareImage = (boolean) getArguments().getSerializable("share_image");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_picture_capture_preview_detail, container, false);
        previewImageView = view.findViewById(R.id.picture_preview_imageview);
        previewImageView.setImageBitmap(image);

        commentEditText = view.findViewById(R.id.picture_comment_edittext);


        cancelSavePictureButton = view.findViewById(R.id.cancel_save_picture_button);

        savePictureButton = view.findViewById(R.id.save_picture_button);

        sharePictureButton = view.findViewById(R.id.share_picture_button);

        sendPictureAsMemoryButton = view.findViewById(R.id.sendMemoryButton);





        cancelSavePictureButton.setOnClickListener(view1->{
            removeFragment(this);
        });

        if(this.shareImage == true){

            sharePictureButton.setVisibility(view.VISIBLE);
            savePictureButton.setVisibility(view.GONE);
            sendPictureAsMemoryButton.setVisibility(view.GONE);
            commentEditText.setVisibility(View.INVISIBLE);

            sharePictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bitmap mBitmap = image;

                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), mBitmap, "temporaryImage",null);
                    Uri uri = Uri.parse(path);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Share Image"));

                }
            });
        }
        else{
            sharePictureButton.setVisibility(view.GONE);
            savePictureButton.setVisibility(view.VISIBLE);
            savePictureButton.setOnClickListener(view1->{
                description = commentEditText.getText().toString();
                boolean isSavedToDisk;
                if (!(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(getActivity(), new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ((MapsActivity)getActivity()).STORAGE_PERMISSION);
                    return;
                }
                boolean isInsertionDone;
                try {
                    isSavedToDisk = ((MapsActivity)getActivity()).saveBitmapToDisk(image, imagePath);
                    String createdOnDate = ((MapsActivity)getActivity()).localDatabaseHelper.getDateTime();
                    isInsertionDone = ((MapsActivity)getActivity()).localDatabaseHelper.insertImagesData(imagePath,lat,lng,description, createdOnDate, owner_name,1, uniqueFileName,owner_id);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    isSavedToDisk = false;
                    return;
                }
                if(isInsertionDone && isSavedToDisk){
                    Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
                    ((MapsActivity)getActivity()).loadImageInformation();
                    ((MapsActivity)getActivity()).makeRenderablePicture();
                    removeFragment(this);
                }
                else{
                    Toast.makeText(getContext(), "Error Occured during saving the image", Toast.LENGTH_LONG).show();
                }
            });

            sendPictureAsMemoryButton.setOnClickListener(view1->{


                description = commentEditText.getText().toString();
                selectContactFragment fragment = selectContactFragment.newInstanceForSendMemories(image, lat, lng, description);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.screen_container, fragment);
                transaction.commit();
//                transaction.addToBackStack(null);
                removeFragment(this);
            });
        }
        return view;
    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
