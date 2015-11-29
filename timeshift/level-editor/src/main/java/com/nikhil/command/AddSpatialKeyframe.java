package com.nikhil.command;

import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.KeyValue;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.keyframe.KeyframeView;
import com.nikhil.view.custom.keyframe.SpatialKeyframeView;

/**
 * Spatial version of adding a keyframe. The requirement of this class is a spatial keyframe in the constructor.
 * Furthermore, any additional configuration must also be mentioned (see {@link AddKeyframe})
 * Created by NikhilVerma on 28/11/15.
 */
public class AddSpatialKeyframe extends AddKeyframe{
    
    private SpatialKeyframeView keyframeCreated;

    /**
     * Creates a add spatial keyframe command
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param manual false, if there is some continuous command that is allowed to follow this command.
     *                       For example: rotation command will happen after a rotation keyframe has been added.
     *                       If this keyframe is created through a manually(lets say through a add keyframe button),
     *                       this value should be true.
     */
    public AddSpatialKeyframe(SpatialKeyframeView keyframeCreated, boolean manual) {
        this.keyframeCreated=keyframeCreated;
        this.manual = manual;
    }

    /**
     * Creates a continuous add spatial keyframe command with final values known
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param continuousCommand the command that will be executed along with this add keyframe command
     */
    public AddSpatialKeyframe(SpatialKeyframeView keyframeCreated, SpatialActionOnSingleItem continuousCommand) {
        this.keyframeCreated=keyframeCreated;
        this.continuousCommand=continuousCommand;
        this.manual = true;
    }
    
    @Override
    public SpatialKeyframeView getKeyframeCreated() {
        return keyframeCreated;
    }

    public void setFinalPosition(UtilPoint finalPosition){
        keyframeCreated.getKeyframeModel().getBezierPoint().setAnchorPoint(finalPosition);
    }

    /**
     * Sets the final value of the bezier point
     * For non manual commands,this method(or setFinal(UtilPoint)) MUST be called at the
     * end of a "continuous" change.
     * @param finalBezierPoint the final bezier point
     */
    public void setFinal(BezierPoint finalBezierPoint) {
        keyframeCreated.getKeyframeModel().setBezierPoint(finalBezierPoint);
    }

    /**
     * Sets the final value of the bezier point by only modifying the anchor point of the keyframe's bezier point
     * For non manual commands,this method(or setFinal(BezierPoint)) MUST be called at the
     * end of a "continuous" change.
     * @param finalAnchorPoint the anchor point to set in the final bezier point
     */
    public void setFinal(UtilPoint finalAnchorPoint) {
        keyframeCreated.getKeyframeModel().getBezierPoint().getAnchorPoint().set(finalAnchorPoint);
    }


}
