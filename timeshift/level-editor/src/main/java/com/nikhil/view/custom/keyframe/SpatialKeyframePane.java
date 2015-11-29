package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.SpatialMetadata;

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
    public void setKeyframeTime(KeyframeView keyframeView, double time) {
        //TODO shift keys and notify change
    }

    @Override
    public int moveSelectedKeysBy(double dl) {
        int keysMoved = super.moveSelectedKeysBy(dl);
        if(keysMoved>0){
            double currentTime = getMetadata().getItemViewController().getCompositionViewController().getTime();
            //TODO set the time of the spatial keyframe change node
        }
        return keysMoved;
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
    }

    /**
     * Removes the specified keyframe from both the pane and the model
     * @param keyframeView the keyframe view to remove(containment check included)
     * @return true if the data structure contained this keyframe(and thus it got removed),
     * false otherwise
     */
    public boolean removeKeyframe(SpatialKeyframeView keyframeView){
        boolean wasRemoved = keyContainer.getChildren().remove(keyframeView);
        if(wasRemoved){
            //remove the value from the change node too
            metadata.getSpatialKeyframeChangeNode().removeKeyframe(keyframeView.getKeyframeModel());
        }
        return wasRemoved;
    }
}
