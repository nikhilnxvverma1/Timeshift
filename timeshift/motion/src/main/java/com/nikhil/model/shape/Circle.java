package com.nikhil.model.shape;

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
public class Circle extends ShapeModel {

	public Circle(float innerRadius,float outerRadius,float startingAngle,float endingAngle){
		//TODO
	}

	//TODO

	public double getInnerRadius() {
		return 0;
	}
	public void setInnerRadius(float innerRadius) {
//		this.innerRadius = innerRadius;
	}
	public double getOuterRadius() {
		return 0;
	}
	public void setOuterRadius(float outerRadius) {
//		this.outerRadius = outerRadius;
	}
	public double getStartingAngle() {
		return 0;
	}
	public void setStartingAngle(float startingAngle) {
//		this.startingAngle = startingAngle;
	}
	public double getEndingAngle() {
		return 0;
	}
	public void setEndingAngle(float endingAngle) {
//		this.endingAngle = endingAngle;
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
