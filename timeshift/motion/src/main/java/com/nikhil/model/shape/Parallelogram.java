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
public class Parallelogram extends ShapeModel {

    public static final int WIDTH_CHANGE_TAG=1;
    public static final int HEIGHT_CHANGE_TAG=2;
    public static final int SWAY_ANGLE_CHANGE_TAG=3;

    protected double width;
    protected TemporalKeyFrame widthStart;
    protected double height;
    protected TemporalKeyFrame heightStart;
    protected float swayAngle;
    protected TemporalKeyFrame swayAngleStart;

    public Parallelogram(double width, double height, float swayAngle) {
        this.width = width;
        this.height = height;
        this.swayAngle = swayAngle;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public TemporalKeyFrame getWidthStart() {
        return widthStart;
    }

    public void setWidthStart(TemporalKeyFrame widthStart) {
        this.widthStart = widthStart;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public TemporalKeyFrame getHeightStart() {
        return heightStart;
    }

    public void setHeightStart(TemporalKeyFrame heightStart) {
        this.heightStart = heightStart;
    }

    public float getSwayAngle() {
        return swayAngle;
    }

    public void setSwayAngle(float swayAngle) {
        this.swayAngle = swayAngle;
    }

    public TemporalKeyFrame getSwayAngleStart() {
        return swayAngleStart;
    }

    public void setSwayAngleStart(TemporalKeyFrame swayAngleStart) {
        this.swayAngleStart = swayAngleStart;
    }

    @Override
    public void registerWithTimeline(Timeline timeline) {
        super.registerWithTimeline(timeline);
        KeyFrame.addAllChangeNodesToTimeline(widthStart, timeline, this, WIDTH_CHANGE_TAG);
        KeyFrame.addAllChangeNodesToTimeline(heightStart, timeline, this, HEIGHT_CHANGE_TAG);
        KeyFrame.addAllChangeNodesToTimeline(swayAngleStart, timeline, this, SWAY_ANGLE_CHANGE_TAG);

    }

    @Override
    public void valueChanged(Timeline timeline, ChangeNode changeNode, KeyValue changedValue) {
        super.valueChanged(timeline, changeNode, changedValue);
        switch (changeNode.tag){
            case WIDTH_CHANGE_TAG:
                width=changedValue.getValue(0);
                break;
            case HEIGHT_CHANGE_TAG:
                height=changedValue.getValue(0);
                break;
            case SWAY_ANGLE_CHANGE_TAG:
                swayAngle=changedValue.getValue(0);
                break;
        }
        this.notifyModelChangeListener();
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
