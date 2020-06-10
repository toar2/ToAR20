package com.aaksoft.toar.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.net.URI;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.google.ar.sceneform.ux.TwistGesture;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/*
    Handles the ARCore sceneview display of model and rotating
 */

public class ModelSceneViewRenderFragment extends Fragment {

    Button backButton;
    SceneView sceneView;
    Uri model = Uri.parse("SEECSv1.2.sfb");
    ObjectAnimator orbitAnimation;

    public ModelSceneViewRenderFragment() {
        // Required empty public constructor
    }

    /*public ModelSceneViewRenderFragment(Uri model){
        this.model = model;
    }*/

    public static ModelSceneViewRenderFragment newInstance(URI curModel) {
        ModelSceneViewRenderFragment fragment = new ModelSceneViewRenderFragment();
        Bundle args = new Bundle();
        args.putSerializable("uri", curModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            URI mModel = (URI) getArguments().getSerializable("uri");
            model = Uri.parse(mModel.toString().replace("%20"," "));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_model_scene_view_render, container, false);

        backButton = view.findViewById(R.id.backButtonPreviewModelFragment);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragement(this);
        });

        sceneView = view.findViewById(R.id.scene_view_model_fragment);

        ModelRenderable.builder()
                .setSource(getContext(), model)
                .build()
                .thenAccept(renderable -> {
                    sceneView = view.findViewById(R.id.scene_view_model_fragment);
                    sceneView.setBackgroundColor(Color.LTGRAY);
                    Camera camera = sceneView.getScene().getCamera();
//                    camera.setLocalPosition(new Vector3(0f, .8f, 3f));

                    if(model.toString().equals("seecs.sfb") || model.toString().equals("parliment.sfb") || model.toString().equals("nice.sfb")){
                        camera.setLocalPosition(new Vector3(0f, 0.8f, 5f));
                    }
                    else if (model.toString().equals("faisalmosque.sfb") || model.toString().equals("centaurus.sfb")){
                        camera.setLocalPosition(new Vector3(0f, 0.3f, 2f));
                    }
                    else if (model.toString().equals("nustgatethree.sfb")){
                        camera.setLocalPosition(new Vector3(0f, 0.3f, 0.5f));
                    }
                    else {
                        camera.setLocalPosition(new Vector3(0f, .8f, 3f));
                    }






                    Scene scene = sceneView.getScene();
                    TransformableNode node = new TransformableNode(((MapsActivity)getActivity()).fragment.getTransformationSystem());

                    node.setParent(scene);
                    node.setName("Andy");
                    node.select();


                    node.setLocalPosition(new Vector3(0f, 0f, 0f));

                    node.setRenderable(renderable);
                    node.setOnTapListener((v, event) -> {
                        orbitAnimation = createAnimator();
                        orbitAnimation.setTarget(node);
                        orbitAnimation.setDuration(10000);
                        orbitAnimation.start();
                        Toast.makeText(getApplicationContext(),"Tapped",Toast.LENGTH_SHORT).show();

                        //node.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0), -90f));
                    });
                })
                .exceptionally( throwable -> {
                    Toast toast = Toast.makeText(getActivity(), "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });

        return view;
    }

    private static ObjectAnimator createAnimator() {
        // Node's setLocalRotation method accepts Quaternions as parameters.
        // First, set up orientations that will animate a circle.
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 120);
        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 240);
        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 360);

        ObjectAnimator orbitAnimation = new ObjectAnimator();
        orbitAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);

        // Next, give it the localRotation property.
        orbitAnimation.setPropertyName("localRotation");

        // Use Sceneform's QuaternionEvaluator.
        orbitAnimation.setEvaluator(new QuaternionEvaluator());

        //  Allow orbitAnimation to repeat forever

        orbitAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        orbitAnimation.setRepeatMode(ObjectAnimator.RESTART);
        orbitAnimation.setInterpolator(new LinearInterpolator());
        orbitAnimation.setAutoCancel(true);

        return orbitAnimation;
    }

    public void removeFragement(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            sceneView.resume();
        } catch (CameraNotAvailableException e) {
            throw new AssertionError("Failed to resume SceneView", e);
        }
    }

    @Override public void onPause() {
        super.onPause();
        sceneView.pause();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        sceneView.destroy();
    }




}
