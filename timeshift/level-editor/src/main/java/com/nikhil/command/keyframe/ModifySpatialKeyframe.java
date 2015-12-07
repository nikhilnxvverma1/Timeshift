package com.nikhil.command.keyframe;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.command.item.SpatialActionOnSingleItem;
import com.nikhil.controller.ItemViewController;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.keyframe.SpatialKeyframeView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NikhilVerma on 28/11/15.
 */
public class ModifySpatialKeyframe extends ItemCommand {

    private SpatialKeyframeView spatialKeyframeView;
    private BezierPoint initialBezierPoint;
    private BezierPoint finalBezierPoint;
    private SpatialActionOnSingleItem continuousCommand;

    public ModifySpatialKeyframe(SpatialKeyframeView spatialKeyframeView, BezierPoint initialBezierPoint) {
        this(spatialKeyframeView, initialBezierPoint,null,null);
    }

    public ModifySpatialKeyframe(SpatialKeyframeView spatialKeyframeView, BezierPoint initialBezierPoint, BezierPoint finalBezierPoint, SpatialActionOnSingleItem continuousCommand) {
        this.spatialKeyframeView = spatialKeyframeView;
        this.initialBezierPoint = initialBezierPoint;
        this.finalBezierPoint = finalBezierPoint;
        this.continuousCommand = continuousCommand;
    }

    public ModifySpatialKeyframe(SpatialKeyframeView spatialKeyframeView, UtilPoint anchorPoint) {
        BezierPoint modifiedBezierPoint = new BezierPoint(spatialKeyframeView.getKeyframeModel().getBezierPoint());
        modifiedBezierPoint.setAnchorPoint(anchorPoint);
        this.spatialKeyframeView = spatialKeyframeView;
        this.initialBezierPoint = modifiedBezierPoint;
    }

    public ModifySpatialKeyframe(SpatialKeyframeView spatialKeyframeView, UtilPoint initialAnchorPoint, UtilPoint finalAnchorPoint, SpatialActionOnSingleItem continuousCommand) {
        this.spatialKeyframeView = spatialKeyframeView;
        this.initialBezierPoint = new BezierPoint(initialAnchorPoint);
        this.finalBezierPoint = new BezierPoint(finalAnchorPoint);
        this.continuousCommand = continuousCommand;
    }

    @Override
    public void execute() {
        spatialKeyframeView.getKeyframeModel().setBezierPoint(finalBezierPoint);
        spatialKeyframeView.updateMotionPath();
        if(continuousCommand!=null){
            continuousCommand.execute();
        }
    }

    @Override
    public void unexecute() {
        spatialKeyframeView.getKeyframeModel().setBezierPoint(initialBezierPoint);
        spatialKeyframeView.updateMotionPath();
        if(continuousCommand!=null){
            continuousCommand.unexecute();//this will take care of reverting back to initial value
        }
    }

    public SpatialKeyframeView getSpatialKeyframeView() {
        return spatialKeyframeView;
    }

    public void setSpatialKeyframeView(SpatialKeyframeView spatialKeyframeView) {
        this.spatialKeyframeView = spatialKeyframeView;
    }

    public SpatialActionOnSingleItem getContinuousCommand() {
        return continuousCommand;
    }

    public void setContinuousCommand(SpatialActionOnSingleItem continuousCommand) {
        this.continuousCommand = continuousCommand;
    }

    /**
     * Sets the final value of the bezier point
     * @param finalBezierPoint the final bezier point
     */
    public void setFinal(BezierPoint finalBezierPoint) {
        this.finalBezierPoint = finalBezierPoint;
    }

    /**
     * Sets the final value of the bezier point by only modifying the anchor point of the keyframe's bezier point
     * @param finalAnchorPoint the anchor point to set in the final bezier point
     */
    public void setFinal(UtilPoint finalAnchorPoint) {
        finalBezierPoint=new BezierPoint(spatialKeyframeView.getKeyframeModel().getBezierPoint());
        finalBezierPoint.setAnchorPoint(finalAnchorPoint);
    }

    /**
     * Checks if this command has been filled or not
     * @return true if this 'Modify Keyframe' command received the end of the change
     */
    public boolean isComplete(){
        return continuousCommand!=null;
    }

    @Override
    public List<ItemViewController> getItemList() {
        LinkedList<ItemViewController> list=new LinkedList<>();
        list.add(spatialKeyframeView.getKeyframePane().getMetadata().getItemViewController());
        return list;
    }
}
