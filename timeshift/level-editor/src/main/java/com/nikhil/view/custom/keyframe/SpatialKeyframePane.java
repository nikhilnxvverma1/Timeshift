package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.view.item.record.SpatialMetadata;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframePane extends KeyframePane{

    private SpatialMetadata metadata;

    public SpatialKeyframePane(double totalTime, double length,SpatialMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
        initKeyframes();
    }

    @Override
    public SpatialMetadata getMetadata() {
        return metadata;
    }

    @Override
    public void keyframesMoved(int totalKeysMoved) {
        double currentTime = getMetadata().getItemViewController().getCompositionViewController().getTime();
        //update the motion path
        updateMotionPath();
        //set the time of the spatial keyframe change node
        metadata.getKeyframeChangeNode().setTime(currentTime);
    }

    @Override
    public SpatialKeyframeView findKeyframeView(Keyframe keyframe) {
        return (SpatialKeyframeView)super.findKeyframeView(keyframe);
    }

    /**
     * Adds the supplied keyframe view to itself. Along with that the keyframe
     * model is also pushed to the spatial keyframe change nod
     * @param keyframeView the keyframe to add to the keyframe pane
     */
    public void addKeyframe(SpatialKeyframeView keyframeView){
        //add the keyframe model to the spatial change node
        metadata.getKeyframeChangeNode().addKeyframe(keyframeView.getKeyframeModel());

        //select it and add it to the "keyContainer" node.
        keyframeView.setSelected(true);
        insertKeyframeWithBezierPoint(keyframeView);
        keyframeView.updateMotionPath();

    }

    /**
     * Adds the keyframe view along with the interactive bezier point
     * @param keyframeView the keyframe view to add
     */
    private void insertKeyframeWithBezierPoint(SpatialKeyframeView keyframeView) {
        keyframeView.setLayoutX(getLayoutXFor(keyframeView));
        keyContainer.getChildren().add(keyframeView);

        //add a new bezier point
        Group outlineGroup = metadata.getItemViewController().getCompositionViewController().getOutlineGroup();
        keyframeView.getInteractiveBezierPoint().addAsChildrenTo(outlineGroup);
    }

    /**
     * Removes the specified keyframe from both the pane and the model
     * @param keyframeView the keyframe view to remove(containment check included)
     * @return true if the data structure contained this keyframe(and thus it got removed),
     * false otherwise
     */
    public boolean removeKeyframe(SpatialKeyframeView keyframeView){
        SpatialKeyframeView previousKeyframeView=findKeyframeView(keyframeView.getKeyframeModel().getPrevious());
        boolean wasRemoved = keyContainer.getChildren().remove(keyframeView);
        if(wasRemoved){
            //remove the value from the change node too
            metadata.getKeyframeChangeNode().removeKeyframe(keyframeView.getKeyframeModel());
            Group worksheetPane = metadata.getItemViewController().getCompositionViewController().getOutlineGroup();
            keyframeView.getInteractiveBezierPoint().removeAsChildrenFrom(worksheetPane);
            if (previousKeyframeView != null) {
                previousKeyframeView.updateMotionPathOnlyForNext();
            }
        }
        return wasRemoved;
    }

    public void showMotionPath(boolean visible){
        for(Node node: keyContainer.getChildren()){
            SpatialKeyframeView keyframeView=(SpatialKeyframeView)node;
            keyframeView.getInteractiveBezierPoint().setVisible(visible);
        }
    }

    /**
     * Updates the entire motion path.
     * This makes a call to update each bezier point O(n^2)
     */
    public void updateMotionPath(){
        for(Node node:keyContainer.getChildren()){
            SpatialKeyframeView keyframeView=(SpatialKeyframeView)node;
            keyframeView.updateMotionPath();
        }
    }

    private void initKeyframes(){
        SpatialKeyframe t=metadata.getKeyframeChangeNode().getStart();
        while(t!=null){
            insertKeyframeWithBezierPoint(new SpatialKeyframeView(t, this));
            t=t.getNext();
        }
        //update the entire motion path (so that cubic curves get updated)
        updateMotionPath();

        //hide the motion path
        showMotionPath(false);
    }
}
