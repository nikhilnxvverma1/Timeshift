package com.nikhil.command;

import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.item.record.Metadata;

/**
 * Enables the stop watch and adds a manual keyframe at that time
 * (A manual keyframe is one that doesn't bring any change).
 * Created by NikhilVerma on 22/11/15.
 */
public class EnableStopWatch extends Command{

    private KeyframeView manualKeyframe;

    public EnableStopWatch(KeyframeView manualKeyframe) {
        this.manualKeyframe = manualKeyframe;
    }

    @Override
    public void execute() {
        //reset the selection of keyframes
        manualKeyframe.getKeyframePane().getMetadata().getItemViewController().
                getCompositionViewController().getKeyframeTable().resetSelection();
        //add the keyframe to its parent keyframe pane
        manualKeyframe.addToParentKeyframePane();

        //make the corresponding metadata keyframable
        Metadata metadata=manualKeyframe.getKeyframePane().getMetadata();
        metadata.setKeyframable(true);
    }

    @Override
    public void unexecute() {
        //remove the keyframe to its parent keyframe pane
        manualKeyframe.removeFromParentKeyframePane();

        //disable the corresponding metadata's keyframable property
        Metadata metadata=manualKeyframe.getKeyframePane().getMetadata();
        metadata.setKeyframable(false);
    }
}
