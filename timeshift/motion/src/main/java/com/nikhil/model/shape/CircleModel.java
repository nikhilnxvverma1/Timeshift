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

	private static int CIRCLE_COUNT=0;

	public static final double DEFAULT_INNER_RADIUS=0;
	public static final double DEFAULT_OUTER_RADIUS=50;
	public static final double DEFAULT_STARTING_ANGLE=0;
	public static final double DEFAULT_ENDING_ANGLE=359.99;

	private TemporalKeyframeChangeNode innerRadiusChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_INNER_RADIUS));
	private TemporalKeyframeChangeNode outerRadiusChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_OUTER_RADIUS));
	private TemporalKeyframeChangeNode startingAngleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_STARTING_ANGLE));
	private TemporalKeyframeChangeNode endingAngleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_ENDING_ANGLE));


	public CircleModel(double innerRadius, double outerRadius, double startingAngle, double endingAngle){
		setInnerRadius(innerRadius);
		setOuterRadius(outerRadius);
		setStartingAngle(startingAngle);
		setEndingAngle(endingAngle);
		this.setName("Circle " + (++CIRCLE_COUNT));
	}

	public double getInnerRadius() {
		return innerRadiusChange.getCurrentValue().get(0);
	}
	public void setInnerRadius(double innerRadius) {
		innerRadiusChange.getCurrentValue().set(0,innerRadius);
	}
	public double getOuterRadius() {
		return outerRadiusChange.getCurrentValue().get(0);
	}
	public void setOuterRadius(double outerRadius) {
		outerRadiusChange.getCurrentValue().set(0,outerRadius);
	}
	public double getStartingAngle() {
		return startingAngleChange.getCurrentValue().get(0);
	}
	public void setStartingAngle(double startingAngle) {
		startingAngleChange.getCurrentValue().set(0,startingAngle);
	}
	public double getEndingAngle() {
		return endingAngleChange.getCurrentValue().get(0);
	}
	public void setEndingAngle(double endingAngle) {
		endingAngleChange.getCurrentValue().set(0,endingAngle);
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
