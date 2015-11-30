package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframePane extends KeyframePane{

    private TemporalMetadata metadata;

    public TemporalKeyframePane(double totalTime, double length,TemporalMetadata metadata) {
        super(totalTime, length);
        this.metadata=metadata;
    }

    @Override
    public TemporalMetadata getMetadata() {
        return metadata;
    }

    /**
     * Adds a new keyframe at the specified time and selects it.
     * Internally it also adds a keyframe model to the associated change node
     * @param time the time of the keyframe
     * @param keyValue the temporal value of the keyframe
     * @return the keyframe view created
     */
    public TemporalKeyframeView addKeyframe(double time, KeyValue keyValue){
        //create and add the keyframe model
        TemporalKeyframe keyframeModel=new TemporalKeyframe(time,keyValue);
        metadata.getTemporalKeyframeChangeNode().addKeyframe(keyframeModel);

        //create a new keyframe view which houses this keyframe mode, select it
        //and add it to the "keyContainer" node.
        TemporalKeyframeView keyframeView=new TemporalKeyframeView(keyframeModel,this);
        keyframeView.setSelected(true);
        keyframeView.setLayoutX(getLayoutXFor(keyframeView));
        keyContainer.getChildren().add(keyframeView);

        return keyframeView;
    }

    /**
     * Adds the supplied keyframe view to itself. Along with that the keyframe
     * model is also pushed to the temporal keyframe change nod
     * @param keyframeView the keyframe to add to the keyframe pane
     */
    public void addKeyframe(TemporalKeyframeView keyframeView){
        //add the keyframe model to the temporal change node
        metadata.getTemporalKeyframeChangeNode().addKeyframe(keyframeView.getKeyframeModel());

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
    public boolean removeKeyframe(TemporalKeyframeView keyframeView){
        boolean wasRemoved = keyContainer.getChildren().remove(keyframeView);
        if(wasRemoved){
            //remove the value from the change node too
            metadata.getTemporalKeyframeChangeNode().removeKeyframe(keyframeView.getKeyframeModel());
        }
        return wasRemoved;
    }

    @Override
    public TemporalKeyframeView findKeyframeView(Keyframe keyframe) {
        return (TemporalKeyframeView)super.findKeyframeView(keyframe);
    }

    @Override
    public void keyframesMoved(int totalKeysMoved) {
        double currentTime = getMetadata().getItemViewController().getCompositionViewController().getTime();
        getMetadata().getTemporalKeyframeChangeNode().setTime(currentTime);
    }
}
