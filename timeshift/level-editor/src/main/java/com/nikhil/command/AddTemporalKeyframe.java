package com.nikhil.command;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.custom.keyframe.TemporalKeyframeView;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Temporal version of adding a keyframe. The requirement of this class is a keyframe in the constructor.
 * Furthermore, any additional configuration must also be mentioned (see {@link AddKeyframe})
 * Created by NikhilVerma on 13/11/15.
 */
public class AddTemporalKeyframe extends AddKeyframe {

    private TemporalKeyframeView keyframeCreated;

    /**
     * Creates a add temporal keyframe command
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param manual false, if there is some continuous command that is allowed to follow this command.
     *                       For example: rotation command will happen after a rotation keyframe has been added.
     *                       If this keyframe is created through a manually(lets say through a add keyframe button),
     *                       this value should be true.
     */
    public AddTemporalKeyframe(TemporalKeyframeView keyframeCreated, boolean manual) {
        this.keyframeCreated=keyframeCreated;
        this.manual = manual;
    }

    /**
     * Creates a continuous add temporal keyframe command with final values known
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param continuousCommand the command that will be executed along with this add keyframe command
     */
    public AddTemporalKeyframe(TemporalKeyframeView keyframeCreated, TemporalActionOnSingleItem continuousCommand) {
        this.keyframeCreated=keyframeCreated;
        this.continuousCommand=continuousCommand;
        this.manual = true;
    }

    @Override
    public TemporalKeyframeView getKeyframeCreated() {
        return keyframeCreated;
    }

    /**
     * Sets the final value of the keyframe.For non manual commands,
     * this method MUST be called at the end of a "continuous" change.
     * @param finalValue the final value of the keyframe when a continuous change ends
     */
    public void setFinalValue(KeyValue finalValue) {
        keyframeCreated.getKeyframeModel().setKeyValue(finalValue);
    }

}
