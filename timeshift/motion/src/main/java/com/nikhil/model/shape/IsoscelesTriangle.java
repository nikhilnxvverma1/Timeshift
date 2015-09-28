package com.nikhil.model.shape;

import com.nikhil.keyframe.KeyFrame;
import com.nikhil.keyframe.TemporalKeyFrame;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Created by NikhilVerma on 20/08/15.
 */
public class IsoscelesTriangle extends ShapeModel {

    public static final int BASE_WIDTH_CHANGE_TAG=1;
    public static final int TIP_ANGLE_CHANGE_TAG=2;

    protected double baseWidth;
    protected TemporalKeyFrame baseWidthStart;
    protected float tipAngle;
    protected TemporalKeyFrame tipAngleStart;

    public IsoscelesTriangle(double baseWidth, float tipAngle) {
        this.baseWidth = baseWidth;
        this.tipAngle = tipAngle;
    }

    public double getBaseWidth() {
        return baseWidth;
    }

    public void setBaseWidth(double baseWidth) {
        this.baseWidth = baseWidth;
    }

    public TemporalKeyFrame getBaseWidthStart() {
        return baseWidthStart;
    }

    public void setBaseWidthStart(TemporalKeyFrame baseWidthStart) {
        this.baseWidthStart = baseWidthStart;
    }

    public float getTipAngle() {
        return tipAngle;
    }

    public void setTipAngle(float tipAngle) {
        this.tipAngle = tipAngle;
    }

    public TemporalKeyFrame getTipAngleStart() {
        return tipAngleStart;
    }

    public void setTipAngleStart(TemporalKeyFrame tipAngleStart) {
        this.tipAngleStart = tipAngleStart;
    }

    @Override
    public void valueChanged(Timeline timeline, ChangeNode changeNode, KeyValue changedValue) {
        super.valueChanged(timeline, changeNode, changedValue);

        switch (changeNode.tag){
            case BASE_WIDTH_CHANGE_TAG:
                baseWidth=changedValue.getValue(0);
                break;
            case TIP_ANGLE_CHANGE_TAG:
                tipAngle=changedValue.getValue(0);
                break;
        }
        this.notifyModelChangeListener();
    }

    @Override
    public void registerWithTimeline(Timeline timeline) {
        super.registerWithTimeline(timeline);
        KeyFrame.addAllChangeNodesToTimeline(baseWidthStart, timeline, this, BASE_WIDTH_CHANGE_TAG);
        KeyFrame.addAllChangeNodesToTimeline(tipAngleStart, timeline, this, TIP_ANGLE_CHANGE_TAG);
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
