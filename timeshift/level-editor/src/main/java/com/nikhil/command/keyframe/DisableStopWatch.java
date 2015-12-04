package com.nikhil.command.keyframe;

import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.item.record.Metadata;

/**
 * Disables the stopwatch and removes all keyframes for this metadata.
 * Undoing the command will bring back the deleted keyframes
 * Created by NikhilVerma on 22/11/15.
 */
public class DisableStopWatch extends ActionOnKeyframes{

    /** A reference ot the metadata is required to set the keyframable property to false.*/
    private Metadata metadata;

    public DisableStopWatch(Metadata metadata){
        this.metadata=metadata;
        keyframeViews=metadata.getKeyframePane().getKeyframes();
    }

    @Override
    public void execute() {
        for(KeyframeView keyframeView:keyframeViews){
            keyframeView.removeFromParentKeyframePane();
        }
        metadata.setKeyframable(false);
    }

    @Override
    public void unexecute() {
        for(KeyframeView keyframeView:keyframeViews){
            keyframeView.addToParentKeyframePane();
        }
        metadata.setKeyframable(true);
    }
}
