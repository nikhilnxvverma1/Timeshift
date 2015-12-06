package com.nikhil.view.custom.keyframe;

import com.nikhil.command.keyframe.ModifySpatialKeyframe;
import com.nikhil.command.item.MoveItem;
import com.nikhil.controller.ItemViewController;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.bezier.BezierPointInteraction;
import com.nikhil.view.bezier.InteractiveBezierPoint;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframeView extends KeyframeView implements BezierPointInteraction {
    private static BezierPoint initialBezierPoint;
    private static UtilPoint initialItemLocation;
    private SpatialKeyframe keyframe;
    private SpatialKeyframePane keyframePane;
    private InteractiveBezierPoint interactiveBezierPoint;

    public SpatialKeyframeView(SpatialKeyframe keyframe,SpatialKeyframePane keyframePane) {
        this(keyframe,keyframePane,DEFAULT_WIDTH);
    }

    public SpatialKeyframeView(SpatialKeyframe keyframe,SpatialKeyframePane keyframePane,double width) {
        super(width);
        this.keyframe = keyframe;
        this.keyframePane=keyframePane;
        this.interactiveBezierPoint=new InteractiveBezierPoint(keyframe.getBezierPoint().getAnchorPoint(),this){
            @Override
            public void addAsChildrenTo(Group group){
                //since points should always have higher Z value
                //prepend the lines and the curve
                group.getChildren().add(previousControlLine);
                group.getChildren().add(nextControlLine);
                if(cubicCurve!=null){
                    group.getChildren().add(cubicCurve);
                }
                //add the points on top of it
                group.getChildren().add(previousControlPointCircle);
                group.getChildren().add(nextControlPointCircle);
                group.getChildren().add(anchorPointRect);


            }//TODO why did we override this?
        };
    }

    public InteractiveBezierPoint getInteractiveBezierPoint() {
        return interactiveBezierPoint;
    }

    @Override
    public SpatialKeyframePane getKeyframePane() {
        return keyframePane;
    }

    @Override
    public SpatialKeyframe getKeyframeModel() {
        return keyframe;
    }

    @Override
    public void addToParentKeyframePane() {
        keyframePane.addKeyframe(this);
    }

    @Override
    public boolean removeFromParentKeyframePane() {
        return keyframePane.removeKeyframe(this);
    }


    /**
     * Updates the motion path w.r.t this keyframe's bezier point.
     * Since the previous is not given, the previous will be searched through in the spatial keyframe pane
     */
    public void updateMotionPath(){
        SpatialKeyframeView previousKeyframeView = keyframePane.findKeyframeView(keyframe.getPrevious());
        updateMotionPath(previousKeyframeView);
    }

    /**
     * Updates the motion path w.r.t this keyframe's bezier point. where the previous is already given
     */
    public void updateMotionPath(SpatialKeyframeView previousKeyframeView){

        //update the anchor point of the interactive bezier point view
        interactiveBezierPoint.set(keyframe.getBezierPoint());
        //update for bezier point after this bezier point
        updateMotionPathOnlyForNext();

        //update the bezier point before this bezier point
        if(previousKeyframeView!=null){
            previousKeyframeView.updateMotionPathOnlyForNext();
        }
    }

    /**
     * Updates the motion path for only the cubic curve till next bezier point.
     * The previous bezier point is not updated.
     */
    public void updateMotionPathOnlyForNext() {
        //TODO point conversion
        SpatialKeyframe nextKeyframe = keyframe.getNext();
        if (nextKeyframe!=null) {
            interactiveBezierPoint.updateView(nextKeyframe.getBezierPoint());
        }else{
            interactiveBezierPoint.updateView(null);

        }

    }

    public boolean isBefore(SpatialKeyframeView keyframeView){
        return keyframe.getNext()==keyframeView.keyframe;
    }

    @Override
    public void bezierPointBeganChanging(InteractiveBezierPoint interactiveBezierPoint, MouseEvent mouseEvent) {
        initialBezierPoint =new BezierPoint(interactiveBezierPoint);
        UtilPoint translation = getKeyframePane().getMetadata().getItemViewController().getTranslation();
        initialItemLocation=new UtilPoint(translation);
    }

    @Override
    public void bezierPointChanging(InteractiveBezierPoint interactiveBezierPoint, MouseEvent mouseEvent) {
        keyframe.getBezierPoint().set(interactiveBezierPoint);
        updateMotionPath();
    }

    @Override
    public void bezierPointFinishedChanging(InteractiveBezierPoint interactiveBezierPoint, MouseEvent mouseEvent) {
        BezierPoint finalBezierPoint=new BezierPoint(interactiveBezierPoint);
        final ItemViewController itemViewController = getKeyframePane().getMetadata().getItemViewController();
        UtilPoint translation = itemViewController.getTranslation();
        UtilPoint finalItemLocation=new UtilPoint(translation);

        MoveItem moveItem=new MoveItem(
                itemViewController,
                initialItemLocation,
                finalItemLocation);
        ModifySpatialKeyframe modifySpatialKeyframe=new ModifySpatialKeyframe(this, initialBezierPoint,finalBezierPoint,moveItem);

        //push but don't execute this command, since the change already made
        itemViewController.getCompositionViewController().getWorkspace().pushCommand(modifySpatialKeyframe,false);
    }

    @Override
    protected void shiftTimeInChangeNode(double time) {
        keyframePane.getMetadata().getSpatialKeyframeChangeNode().shiftKeyframe(keyframe,time);
    }
}
