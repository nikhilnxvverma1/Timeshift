package com.nikhil.model.shape;

import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.change.ChangeNodeIterator;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.KeyValue;

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
	private TemporalKeyframeChangeNode startAngleChange =new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_STARTING_ANGLE));
	private TemporalKeyframeChangeNode endAngleChange =new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_ENDING_ANGLE));


	public CircleModel() {
		this(DEFAULT_INNER_RADIUS,DEFAULT_OUTER_RADIUS,DEFAULT_STARTING_ANGLE,DEFAULT_ENDING_ANGLE);
	}

	public CircleModel(double innerRadius, double outerRadius, double startingAngle, double endingAngle){
		setInnerRadius(innerRadius);
		setOuterRadius(outerRadius);
		setStartAngle(startingAngle);
		setEndAngle(endingAngle);
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
	public double getStartAngle() {
		return startAngleChange.getCurrentValue().get(0);
	}
	public void setStartAngle(double startingAngle) {
		startAngleChange.getCurrentValue().set(0,startingAngle);
	}
	public double getEndAngle() {
		return endAngleChange.getCurrentValue().get(0);
	}
	public void setEndAngle(double endingAngle) {
		endAngleChange.getCurrentValue().set(0,endingAngle);
	}

	public TemporalKeyframeChangeNode innerRadiusChange() {
		return innerRadiusChange;
	}

	public TemporalKeyframeChangeNode outerRadiusChange() {
		return outerRadiusChange;
	}

	public TemporalKeyframeChangeNode startAngleChange() {
		return startAngleChange;
	}

	public TemporalKeyframeChangeNode endAngleChange() {
		return endAngleChange;
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public ChangeNodeIterator changeNodeIterator() {
		ChangeNode[] additional=new ChangeNode[4];
		additional[0]=innerRadiusChange;
		additional[1]=outerRadiusChange;
		additional[2]=startAngleChange;
		additional[3]=endAngleChange;
		return new ChangeNodeIterator(shapeChangeNodes(),additional);
	}
}
