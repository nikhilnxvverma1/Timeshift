package com.nikhil.model.shape;

import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Circle consists of the following properties:
 * <ul>
 *     <li>Inner radius</li>
 *     <li>Outer radius</li>
 *     <li>Starting angle</li>
 *     <li>Ending angle</li>
 * </ul>
 */
public class CircleModel extends ShapeModel {

	public static final double DEFAULT_INNER_RADIUS=0;
	public static final double DEFAULT_OUTER_RADIUS=50;
	public static final double DEFAULT_STARTING_ANGLE=0;
	public static final double DEFAULT_ENDING_ANGLE=359;

	private TemporalKeyframeChangeNode innerRadiusChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_INNER_RADIUS));
	private TemporalKeyframeChangeNode outerRadiusChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_OUTER_RADIUS));
	private TemporalKeyframeChangeNode startingAngleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_STARTING_ANGLE));
	private TemporalKeyframeChangeNode endingAngleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_ENDING_ANGLE));


	public CircleModel(float innerRadius, float outerRadius, float startingAngle, float endingAngle){
		setInnerRadius(innerRadius);
		setOuterRadius(outerRadius);
		setStartingAngle(startingAngle);
		setEndingAngle(endingAngle);
	}

	public double getInnerRadius() {
		return innerRadiusChange.getCurrentValue().get(0);
	}
	public void setInnerRadius(float innerRadius) {
		innerRadiusChange.getCurrentValue().set(0,innerRadius);
	}
	public double getOuterRadius() {
		return outerRadiusChange.getCurrentValue().get(0);
	}
	public void setOuterRadius(float outerRadius) {
		outerRadiusChange.getCurrentValue().set(0,outerRadius);
	}
	public double getStartingAngle() {
		return startingAngleChange.getCurrentValue().get(0);
	}
	public void setStartingAngle(float startingAngle) {
		startingAngleChange.getCurrentValue().set(0,startingAngle);
	}
	public double getEndingAngle() {
		return endingAngleChange.getCurrentValue().get(0);
	}
	public void setEndingAngle(float endingAngle) {
		endingAngleChange.getCurrentValue().set(0,endingAngle);
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
