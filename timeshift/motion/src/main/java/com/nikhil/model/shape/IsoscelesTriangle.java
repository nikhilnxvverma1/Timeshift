package com.nikhil.model.shape;

import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Isosceles triangle model consists of the following properties:
 * <ul>
 *     <li>Base</li>
 *     <li>Height</li>
 * </ul>
 * Created by NikhilVerma on 20/08/15.
 */
public class IsoscelesTriangle extends ShapeModel {

    public IsoscelesTriangle(double baseWidth, float tipAngle) {
        //TODO
    }

    //TODO

    public double getBase() {
        return 0;
    }

    public void setBase(double base) {
//        this.base = base;
    }

    public double getHeight() {
        return 0;
    }

    public void setHeight(double height) {
//        this.height = height;
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }
}
