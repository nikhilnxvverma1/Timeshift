package com.nikhil.model.shape;

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
public class Parallelogram extends ShapeModel {


    public Parallelogram(double width, double height, float swayAngle) {
        //TODO
    }

    //TODO

    public double getWidth() {
        return 0;
    }

    public void setWidth(double width) {
//        this.width = width;
    }

    public double getHeight() {
        return 0;
    }

    public void setHeight(double height) {
//        this.height = height;
    }

    public double getSwayAngle() {
        return 0;
    }

    public void setSwayAngle(float swayAngle) {
//        this.swayAngle = swayAngle;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
