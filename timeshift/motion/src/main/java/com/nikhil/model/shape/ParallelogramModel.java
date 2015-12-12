package com.nikhil.model.shape;

import com.nikhil.timeline.change.ChangeNodeIterator;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Parallelogram model consists of the folowing properties:
 * <ul>
 *     <li>Width</li>
 *     <li>Height</li>
 *     <li>Sway angle</li>
 * </ul>
 * Created by NikhilVerma on 20/08/15.
 */
public class ParallelogramModel extends ShapeModel {

    private static int parallelogramCount=0;

    public static final double DEFAULT_WIDTH=10;
    public static final double DEFAULT_HEIGHT=10;
    public static final double DEFAULT_SWAY_ANGLE=0;


    private TemporalKeyframeChangeNode widthChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_WIDTH));
    private TemporalKeyframeChangeNode heightChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_HEIGHT));
    private TemporalKeyframeChangeNode swayAngleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_SWAY_ANGLE));


    public ParallelogramModel(double width, double height, double swayAngle) {
        setWidth(width);
        setHeight(height);
        setSwayAngle(swayAngle);
        this.setName("Parallelogram "+(++parallelogramCount));
    }

    public ParallelogramModel() {
        this(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_SWAY_ANGLE);
    }

    public double getWidth() {
        return 0;
    }

    public void setWidth(double width) {
        widthChange.getCurrentValue().set(0,width);
    }

    public double getHeight() {
        return 0;
    }

    public void setHeight(double height) {
        heightChange.getCurrentValue().set(0,height);
    }

    public double getSwayAngle() {
        return 0;
    }

    public void setSwayAngle(double swayAngle) {
        swayAngleChange.getCurrentValue().set(0,swayAngle);
    }

    public TemporalKeyframeChangeNode widthChange(){
        return widthChange;
    }

    public TemporalKeyframeChangeNode heightChange(){
        return heightChange;
    }

    public TemporalKeyframeChangeNode swayAngleChange(){
        return swayAngleChange;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ChangeNodeIterator changeNodeIterator() {
        ChangeNode[] additional=new ChangeNode[3];
        additional[0]=widthChange;
        additional[1]=heightChange;
        additional[2]=swayAngleChange;
        return new ChangeNodeIterator(shapeChangeNodes(),additional);
    }
}
