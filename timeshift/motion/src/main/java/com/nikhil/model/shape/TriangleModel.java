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
 * Triangle model consists of the following properties:
 * <ul>
 *     <li>Base</li>
 *     <li>Height</li>
 * </ul>
 * Created by NikhilVerma on 20/08/15.
 */
public class TriangleModel extends ShapeModel {

    private static int triangleCount=0;
    public static final double DEFAULT_BASE=10;
    public static final double DEFAULT_HEIGHT=20;

    private TemporalKeyframeChangeNode baseChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_BASE));
    private TemporalKeyframeChangeNode heightChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_HEIGHT));

    public TriangleModel() {
        this(DEFAULT_BASE,DEFAULT_HEIGHT);
    }

    public TriangleModel(double base, double height) {
        setBase(base);
        setHeight(height);
        this.setName("Triangle"+(++triangleCount));
    }

    public TemporalKeyframeChangeNode baseChange() {
        return baseChange;
    }

    public void setBase(double base) {
        this.baseChange.getCurrentValue().set(0,base);
    }

    public double getBase(){
        return this.baseChange.getCurrentValue().get(0);
    }

    public TemporalKeyframeChangeNode heightChange() {
        return heightChange;
    }

    public void setHeight(double height) {
        this.heightChange.getCurrentValue().set(0,height);
    }

    public double getHeight(){
        return this.heightChange.getCurrentValue().get(0);
    }

    @Override
    public void acceptVisitor(ModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ChangeNodeIterator changeNodeIterator() {
        ChangeNode[] additional=new ChangeNode[2];
        additional[0]=baseChange;
        additional[1]=heightChange;
        return new ChangeNodeIterator(shapeChangeNodes(),additional);
    }
}
