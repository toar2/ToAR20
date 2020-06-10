package uk.co.appoly.arcorelocation.rendering;

import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.LocationUtils;

public class LocationNode extends AnchorNode {

    private String TAG = "LocationNode";

    private LocationMarker locationMarker;
    private LocationNodeRender renderEvent;
    private int distance;
    private double distanceInAR;
    private float scaleModifier = 1F;
    private float height = 0F;
    private float gradualScalingMinScale = 0.8F;
    private float gradualScalingMaxScale = 1.4F;

    private LocationMarker.ScalingMode scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN;
    private LocationScene locationScene;

    public LocationNode(Anchor anchor, LocationMarker locationMarker, LocationScene locationScene) {
        super(anchor);
        this.locationMarker = locationMarker;
        this.locationScene = locationScene;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScaleModifier() {
        return scaleModifier;
    }

    public void setScaleModifier(float scaleModifier) {
        this.scaleModifier = scaleModifier;
    }

    public LocationNodeRender getRenderEvent() {
        return renderEvent;
    }

    public void setRenderEvent(LocationNodeRender renderEvent) {
        this.renderEvent = renderEvent;
    }

    public int getDistance() {
        return distance;
    }

    public double getDistanceInAR() {
        return distanceInAR;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setDistanceInAR(double distanceInAR) {
        this.distanceInAR = distanceInAR;
    }

    public LocationMarker.ScalingMode getScalingMode() {
        return scalingMode;
    }

    public void setScalingMode(LocationMarker.ScalingMode scalingMode) {
        this.scalingMode = scalingMode;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        // Typically, getScene() will never return null because onUpdate() is only called when the node
        // is in the scene.
        // However, if onUpdate is called explicitly or if the node is removed from the scene on a
        // different thread during onUpdate, then getScene may be null.


        for (Node n : getChildren()) {
            if (getScene() == null) {
                return;
            }

            Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
            Vector3 nodePosition = n.getWorldPosition();

            // Compute the difference vector between the camera and anchor
            float dx = cameraPosition.x - nodePosition.x;
            float dy = cameraPosition.y - nodePosition.y;
            float dz = cameraPosition.z - nodePosition.z;

            // Compute the straight-line distance.
            setDistanceInAR(Math.sqrt(dx * dx + dy * dy + dz * dz));


            if (locationScene.shouldOffsetOverlapping()) {
                /*List<Node> nodeScene = locationScene.mArSceneView.getScene().getChildren();
                for(Node node: nodeScene){
                    Vector3 nv1 = node.getLocalPosition();
                    for( Node node1: nodeScene){
                        Vector3 nv2 = node1.getLocalPosition();
                        if(nv1 != nv2){
                            nv1.
                        }
                    }

                }*/
                /*for(Node node: locationScene.mArSceneView.getScene().getChildren()){
                    Vector3 n1pos = n.getWorldPosition();
                    Vector3 n2pos = node.getWorldPosition();
                    double distance = Math.sqrt(Math.pow((n1pos.x- n2pos.x),2) + Math.pow((getHeight()-n2pos.y),2) + Math.pow((n1pos.z- n2pos.z),2));
                    if(distance < 10){
                        setHeight(getHeight() + 2.2F);
                    }

                }*/

                ArrayList<Node> overLappingNodes = locationScene.mArSceneView.getScene().overlapTestAll(n);
                //locationScene.mArSceneView.getScene().
                if (overLappingNodes.size() > 0) {
                    //for( Node overLapNode: overLappingNodes){
                    //    locationScene.mArSceneView.getScene().removeChild(overLapNode);
                    //}
                    setHeight(getHeight() + 2.2F);// + 1.2
                }

            }
        }

        if(!locationScene.minimalRefreshing())
            scaleAndRotate();


        if (renderEvent != null) {
            if(this.isTracking() && this.isActive() && this.isEnabled())
                renderEvent.render(this);
        }

    }

    public void scaleAndRotate() {

        for (Node n : getChildren()) {
            int markerDistance = (int) Math.ceil(
                    LocationUtils.distance(
                            locationMarker.latitude,
                            locationScene.deviceLocation.currentBestLocation.getLatitude(),
                            locationMarker.longitude,
                            locationScene.deviceLocation.currentBestLocation.getLongitude(),
                            0,
                            0)
            );

            setDistance(markerDistance);

            // Limit the distance of the Anchor within the scene.
            // Prevents uk.co.appoly.arcorelocation.rendering issues.
            int renderDistance = markerDistance;
            if (renderDistance > locationScene.getDistanceLimit())
                renderDistance = locationScene.getDistanceLimit();

            float scale = 1F;

            switch (scalingMode) {

                // Make sure marker stays the same size on screen, no matter the distance
                case FIXED_SIZE_ON_SCREEN:

                    if(locationMarker.getTag().equals("")) {    //for places markers

                        int allotedSlab = markerDistance % 4;

                        if(allotedSlab == 0){
                            setHeight(getHeight()- 2.5f);
                        }
                        else if(allotedSlab == 1){
                            setHeight(getHeight());
                        }
                        else if(allotedSlab == 2){
                            setHeight(getHeight()+ 2.5f);
                        }
                        else if(allotedSlab == 3){
                            setHeight(getHeight()+ 5.0f);
                        }

                        if (markerDistance >= 0 && markerDistance < 400) {
                            scale = 9f;
                        }
                        // Distant markers a little smaller
                        else if (markerDistance >= 400 && markerDistance < 600) {
                            scale = 8f;
                        }
                        else if (markerDistance >= 600 && markerDistance < 1000) {
                            scale = 7f;
                        }
                        else if(markerDistance >= 1000){
                            scale = 6f;
                        }
                    }

                    else{
                        scale = 0.5F * (float) renderDistance;
                    }

                    break;

                case GRADUAL_TO_MAX_RENDER_DISTANCE:
                    float scaleDifference = gradualScalingMaxScale - gradualScalingMinScale;
                    scale = (gradualScalingMinScale + ((locationScene.getDistanceLimit() - markerDistance) * (scaleDifference / locationScene.getDistanceLimit()))) * renderDistance;
                    break;
            }


            scale *= scaleModifier;

            Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
            Vector3 nodePosition = n.getWorldPosition();
            n.setWorldPosition(new Vector3(n.getWorldPosition().x, getHeight(), n.getWorldPosition().z));
            Vector3 direction = Vector3.subtract(cameraPosition, nodePosition);
            Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());

            n.setWorldRotation(lookRotation);
            //locationMarker.node.setWorldScale(new Vector3(scale, scale, scale));
            n.setWorldScale(new Vector3(scale, scale, scale));



        }
    }

    public float getGradualScalingMinScale() {
        return gradualScalingMinScale;
    }

    public void setGradualScalingMinScale(float gradualScalingMinScale) {
        this.gradualScalingMinScale = gradualScalingMinScale;
    }

    public float getGradualScalingMaxScale() {
        return gradualScalingMaxScale;
    }

    public void setGradualScalingMaxScale(float gradualScalingMaxScale) {
        this.gradualScalingMaxScale = gradualScalingMaxScale;
    }
}
