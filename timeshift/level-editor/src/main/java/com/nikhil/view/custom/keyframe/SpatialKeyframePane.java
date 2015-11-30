package com.nikhil.view.custom.keyframe;

import com.nikhil.logging.Logger;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.SpatialMetadata;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframePane extends KeyframePane{

    private SpatialMetadata metadata;

    public SpatialKeyframePane(double totalTime, double length,SpatialMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
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
        metadata.getSpatialKeyframeChangeNode().setTime(currentTime);
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
        metadata.getSpatialKeyframeChangeNode().addKeyframe(keyframeView.getKeyframeModel());

        //select it and add it to the "keyContainer" node.
        keyframeView.setSelected(true);
        keyframeView.setLayoutX(getLayoutXFor(keyframeView));
        keyContainer.getChildren().add(keyframeView);

        //add a new bezier point
        Pane worksheetPane = metadata.getItemViewController().getCompositionViewController().getWorkspace().getWorksheetPane();
        keyframeView.getInteractiveBezierPoint().addAsChildrenTo(worksheetPane);
        keyframeView.updateMotionPath();

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
            metadata.getSpatialKeyframeChangeNode().removeKeyframe(keyframeView.getKeyframeModel());
            Pane worksheetPane = metadata.getItemViewController().getCompositionViewController().getWorkspace().getWorksheetPane();
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
}
