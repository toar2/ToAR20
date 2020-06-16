package com.aaksoft.toar.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.fragments.UserPictureCapturePreviewDetailFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class augmentModels extends AppCompatActivity {


    ArFragment arFragment;
    ImageView serena, centaurus, faisalmosque, monument, library, convention,
                parliment, nice, nustgatethree, nustgateten, nustmosque, seecs;
    LinearLayout optionsPanel;


    String[] modelsInfo;
    String[] modelsNames;

    String uniqueUserID, userName;
    private double latitude, longitude;

    Button informationButton, takePictureButton, navigationButton;

    View[] arrayView;

    private ModelRenderable serenaRenderable,centaurusRenderable,
            faisalmosqueRenderable,monumentRenderable,libraryRenderable, conventionRenderable,
            parlimentRenderable, niceRenderable, nustgatethreeRenderable, nustgatetenRenderable,nustmosqueRenderable, seecsRenderable;

    int selected;
    boolean isModelBeingAugmented;
    String currentModelAugmented;
    AnchorNode anchorNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        isModelBeingAugmented = false;

        uniqueUserID = getIntent().getStringExtra("uniqueUserID");
        userName = getIntent().getStringExtra("userName");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);


        selected = 1;           // select the first model by default

        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_augment_models);

        arFragment =(ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        // hiding the instruction view on our arfragment
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);



        informationButton = (Button)findViewById(R.id.infoButton);
        takePictureButton = (Button)findViewById(R.id.picturebutton);
        navigationButton = (Button)findViewById(R.id.navigateButton);
        optionsPanel = (LinearLayout)findViewById(R.id.optionsPanel);         // invisible by default.
                                                                                // visible when model is augmented


        serena = (ImageView)findViewById(R.id.serena);
        centaurus = (ImageView)findViewById(R.id.centaurus);
        faisalmosque = (ImageView)findViewById(R.id.faisalmosque);
        monument = (ImageView)findViewById(R.id.monument);
        library = (ImageView)findViewById(R.id.nustlibrary);  //At other places 'library' was used
        convention = (ImageView)findViewById(R.id.convention);
        parliment = (ImageView)findViewById(R.id.parliment);
        nice = (ImageView)findViewById(R.id.nice);
        nustgatethree = (ImageView)findViewById(R.id.nustgatethree);
        nustgateten = (ImageView)findViewById(R.id.nustgateten);
        nustmosque = (ImageView)findViewById(R.id.nustmosque);
        seecs = (ImageView)findViewById(R.id.seecs);

        setArrayView();
        setBackground(arrayView[0].getId());            // Select serena by default
        setClickListener();
        setupModel();

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {


                    if(isModelBeingAugmented){
                        anchorNode.setParent(null);
                    }

                    optionsPanel.setVisibility(View.GONE);
                    Anchor anchor= hitResult.createAnchor();
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    isModelBeingAugmented = true;

                    createModel(anchorNode, selected);
                    optionsPanel.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(isModelBeingAugmented){                  // if model was being augmented, stop the augmentation
            anchorNode.setParent(null);
            isModelBeingAugmented = false;
            optionsPanel.setVisibility(View.GONE);
        }
        selected = 1;
        setBackground(arrayView[0].getId());
    }
    @Override
    protected void onStop(){
        super.onStop();

        if(isModelBeingAugmented){
            anchorNode.setParent(null);             // if model was being augmented, stop the augmentation
            isModelBeingAugmented = false;
            optionsPanel.setVisibility(View.GONE);
        }
        selected = 1;
        setBackground(arrayView[0].getId());
    }


    private void setupModel() {



        ModelRenderable.builder()
                .setSource(this, Uri.parse("serena.sfb"))
                .build().thenAccept(renderable -> serenaRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load SERENA model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("centaurus.sfb"))
                .build().thenAccept(renderable -> centaurusRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load centaurus model", Toast.LENGTH_SHORT).show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("faisalmosque.sfb"))
                .build().thenAccept(renderable -> faisalmosqueRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load faisalmosque model", Toast.LENGTH_SHORT).show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("monument.sfb"))
                .build().thenAccept(renderable -> monumentRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load monument model", Toast.LENGTH_SHORT).show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("library.sfb"))
                .build().thenAccept(renderable -> libraryRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load library model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("convention.sfb"))
                .build().thenAccept(renderable -> conventionRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load convention model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("parliment.sfb"))
                .build().thenAccept(renderable -> parlimentRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load parliment model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("nice.sfb"))
                .build().thenAccept(renderable -> niceRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load NICE model", Toast.LENGTH_SHORT).show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("nustgatethree.sfb"))
                .build().thenAccept(renderable -> nustgatethreeRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load nustgatethree model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("nustgateten.sfb"))
                .build().thenAccept(renderable -> nustgatetenRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load nustgateten model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("nustmosque.sfb"))
                .build().thenAccept(renderable -> nustmosqueRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load nustmosque model", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, Uri.parse("seecs.sfb"))
                .build().thenAccept(renderable -> seecsRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load seecs model", Toast.LENGTH_SHORT).show();
                            return null;
                        });}


    private void createModel(AnchorNode anchorNode, int selected) {

        if(selected == 1)

        {
            TransformableNode serena = new TransformableNode(arFragment.getTransformationSystem());
            serena.setParent(anchorNode);
            serena.setRenderable(serenaRenderable);
            serena.select();
            currentModelAugmented = "serena";
            addName(anchorNode,serena,"SERENA");

        }
        if(selected == 2)
        {
            TransformableNode centaurus = new TransformableNode(arFragment.getTransformationSystem());
            centaurus.setParent(anchorNode);
            centaurus.setRenderable(centaurusRenderable);
            centaurus.select();
            currentModelAugmented = "centaurus";
            addName(anchorNode,centaurus,"centaurus");

        }

        if(selected == 3)
        {
            TransformableNode faisalmosque = new TransformableNode(arFragment.getTransformationSystem());
            faisalmosque.setParent(anchorNode);
            faisalmosque.setRenderable(faisalmosqueRenderable);
            faisalmosque.select();
            currentModelAugmented = "faisalmosque";
            addName(anchorNode,faisalmosque,"faisalmosque");

        }
        if(selected == 4)
        {
            TransformableNode monument = new TransformableNode(arFragment.getTransformationSystem());
            monument.setParent(anchorNode);
            monument.setRenderable(monumentRenderable);
            monument.select();
            currentModelAugmented = "monument";
            addName(anchorNode,monument,"monument");

        }
        if(selected == 5)
        {
            TransformableNode library = new TransformableNode(arFragment.getTransformationSystem());
            library.setParent(anchorNode);
            library.setRenderable(libraryRenderable);
            library.select();
            currentModelAugmented = "library";
            addName(anchorNode,library,"library");

        }
        if(selected == 6)
        {
            TransformableNode convention = new TransformableNode(arFragment.getTransformationSystem());
            convention.setParent(anchorNode);
            convention.setRenderable(conventionRenderable);
            convention.select();
            currentModelAugmented = "convention";
            addName(anchorNode,convention,"convention");

        }

        if(selected == 7)
        {
            TransformableNode parliment = new TransformableNode(arFragment.getTransformationSystem());
            parliment .setParent(anchorNode);
            parliment .setRenderable(parlimentRenderable);
            parliment .select();
            currentModelAugmented = "parliment";
            addName(anchorNode,parliment ,"parliment ");

        }
        if(selected == 8)
        {
            TransformableNode nice = new TransformableNode(arFragment.getTransformationSystem());
            nice.setParent(anchorNode);
            nice.setRenderable(niceRenderable);
            nice.select();
            currentModelAugmented = "nice";
            addName(anchorNode,nice,"nice");

        }
        if(selected == 9)
        {
            TransformableNode nustgatethree = new TransformableNode(arFragment.getTransformationSystem());
            nustgatethree.setParent(anchorNode);
            nustgatethree.setRenderable(nustgatethreeRenderable);
            nustgatethree.select();
            currentModelAugmented = "nustgatethree";
            addName(anchorNode,nustgatethree,"nustgatethree");

        }
        if(selected == 10)
        {
            TransformableNode nustgateten = new TransformableNode(arFragment.getTransformationSystem());
            nustgateten.setParent(anchorNode);
            nustgateten.setRenderable(nustgatetenRenderable);
            nustgateten.select();
            currentModelAugmented = "nustgateten";
            addName(anchorNode,nustgateten,"nustgateten");

        }
        if(selected == 11)
        {
            TransformableNode nustmosque = new TransformableNode(arFragment.getTransformationSystem());
            nustmosque.setParent(anchorNode);
            nustmosque.setRenderable(nustmosqueRenderable);
            nustmosque.select();
            currentModelAugmented = "nustmosque";
            addName(anchorNode,nustmosque,"nustmosque");

        }
        if(selected == 12)
        {
            TransformableNode seecs = new TransformableNode(arFragment.getTransformationSystem());
            seecs.setParent(anchorNode);
            seecs.setRenderable(seecsRenderable);
            seecs.select();
            currentModelAugmented = "seecs";
            addName(anchorNode,seecs,"seecs");

        }

    }

    private void addName(AnchorNode anchorNode, TransformableNode model, String name) {
       /* final ViewRenderable name_building;
        ViewRenderable.builder()
                        .setView(this, R.layout.name_building)
                        .build()
                        .thenAccept(renderable ->name_building = renderable );
*/
        ViewRenderable.builder().setView(this,R.layout.name_building)
                .build()
                .thenAccept(viewRenderable -> {

                    TransformableNode nameView = new TransformableNode(arFragment.getTransformationSystem());
                    nameView.setLocalPosition(new Vector3(0f,model.getLocalPosition().y+1.0f,0));
                    nameView.setParent(anchorNode);
                    nameView.setRenderable(viewRenderable);
                    nameView.select();

                    TextView txt_name = (TextView)viewRenderable.getView();
                    txt_name.setText(name);
                    txt_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            anchorNode.setParent(null);
                            isModelBeingAugmented = false;
                            optionsPanel.setVisibility(View.GONE);
                        }
                    });

                });




    }

    private void setClickListener() {
        arrayView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 1;
                setBackground(view.getId());

            }
        });
        arrayView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 2;
                setBackground(view.getId());
            }
        });
        arrayView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 3;
                setBackground(view.getId());

            }
        });
        arrayView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 4;
                setBackground(view.getId());
            }
        });
        arrayView[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 5;
                setBackground(view.getId());
            }
        });
        arrayView[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 6;
                setBackground(view.getId());
            }
        });
        arrayView[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 7;
                setBackground(view.getId());
            }
        });
        arrayView[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 8;
                setBackground(view.getId());
            }
        });
        arrayView[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 9;
                setBackground(view.getId());
            }
        });
        arrayView[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 10;
                setBackground(view.getId());
            }
        });
        arrayView[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 11;
                setBackground(view.getId());
            }
        });
        arrayView[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 12;
                setBackground(view.getId());
            }
        });


        // TODO: Add functionality to the following
        informationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(modelsInfo[selected-1])));
//                Toast.makeText(getApplicationContext(), "Information for " + currentModelAugmented, Toast.LENGTH_LONG).show();

            }
        });
        takePictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

//                Toast.makeText(getApplicationContext(), userName + " " + uniqueUserID + " " + latitude + " " + longitude, Toast.LENGTH_LONG).show();
                takePhoto();

            }
        });
        navigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent navigateToLocationIntent = new Intent(getApplicationContext(), MapsActivity.class);
                navigateToLocationIntent.putExtra("navigate", "yes");
                navigateToLocationIntent.putExtra("navigateToModelNumber", selected);
                startActivity(navigateToLocationIntent);



            }
        });
    }

    private void setArrayView()
    {
        arrayView = new View[]
                {
                        serena, centaurus, faisalmosque, monument, library, convention, parliment, nice, nustgatethree, nustgateten, nustmosque, seecs

                } ;
        modelsInfo = new String[] {"https://www.serenahotels.com/serenaislamabad/en/default.html","https://thecentaurusmall.com/", "https://www.lonelyplanet.com/pakistan/islamabad-and-rawalpindi/attractions/shah-faisal-mosque/a/poi-sig/464356/357196", "http://phcsingapore.org/emergingpakistan/pakistan-monument.html", "http://www.nust.edu.pk/Library/Pages/default.aspx", "https://en.wikipedia.org/wiki/Jinnah_Convention_Centre", "http://www.senate.gov.pk/en/parliament.php?id=-1&catid=4&subcatid=267&cattitle=Parliament%20House", "http://www.nust.edu.pk/INSTITUTIONS/Schools/SCEE/Institutes/NICE/Pages/default.aspx","http://www.nust.edu.pk/Pages/Default.aspx", "http://www.nust.edu.pk/Pages/Default.aspx", "http://www.nust.edu.pk/News/Pages/Khatm-e-Quran-Ceremonies-held-at-NUST-Mosques.aspx", "http://www.nust.edu.pk/INSTITUTIONS/Schools/SEECS/Pages/default.aspx"};

        modelsNames = new String[] {"serena.sfb", "centaurus.sfb", "faisalmosque.sfb", "monument.sfb", "library.sfb", "convention.sfb" ,"parliment.sfb", "nice.sfb", "nustgatethree.sfb", "nustgateten.sfb", "nustmosque.sfb", "seecs.sfb"};

    }

    // Changes background color of selected model picture and makes everything else transparent
    private void setBackground(int id) {
        for(int i=0;i<arrayView.length;i++)
        {
            if(arrayView[i].getId() == id)
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"));
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT);

        }
    }



    private void takePhoto() {
        final String uniqueFileName = generateUniqueImageName();
        final String localFilePath = generateFilePathLocal(uniqueFileName);
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                augmentModels.this.runOnUiThread(new Runnable() {
                    public void run() {
                        UserPictureCapturePreviewDetailFragment userPictureCapturePreviewDetailFragment = UserPictureCapturePreviewDetailFragment.newInstance(bitmap, uniqueFileName, localFilePath, latitude, longitude, uniqueUserID, userName, true);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.screen_container2, userPictureCapturePreviewDetailFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            } else {
                Toast toast = Toast.makeText(this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    private String generateUniqueImageName(){
        String date = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        String uniqueFileName = uniqueUserID + "_" + date + "_image.jpg";
        return uniqueFileName;
    }

    private String generateFilePathLocal(String uniqueFileName) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ToAR/" + uniqueUserID + "/" + uniqueFileName;
    }


}
