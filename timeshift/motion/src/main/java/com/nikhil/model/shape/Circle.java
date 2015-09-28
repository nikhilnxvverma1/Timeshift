package com.nikhil.model.shape;

import com.nikhil.keyframe.KeyFrame;
import com.nikhil.keyframe.TemporalKeyFrame;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

public class Circle extends ShapeModel {

	public static final int INNER_RADIUS_HANDLE =1;
	public static final int OUTER_RADIUS_HANDLE =2;
	public static final int STARTING_ANGLE_HANDLE =3;
	public static final int ENDING_ANGLE_HANDLE =4;
	
	
	private float innerRadius;
	private TemporalKeyFrame innerRadiusStart;
	
	private float outerRadius;
	private TemporalKeyFrame outerRadiusStart;
	
	private float startingAngle;
	private TemporalKeyFrame startingAngleStart;
	
	private float endingAngle;
	private TemporalKeyFrame endingAngleStart;
	
	public Circle(float innerRadius,float outerRadius,float startingAngle,float endingAngle){
		this.innerRadius=innerRadius;
		this.outerRadius=outerRadius;
		this.startingAngle=startingAngle;
		this.endingAngle=endingAngle;
	}
	
	//================================================================================
	//Accessors
	//================================================================================
	
	public float getInnerRadius() {
		return innerRadius;
	}
	public void setInnerRadius(float innerRadius) {
		this.innerRadius = innerRadius;
	}
	public float getOuterRadius() {
		return outerRadius;
	}
	public void setOuterRadius(float outerRadius) {
		this.outerRadius = outerRadius;
	}
	public float getStartingAngle() {
		return startingAngle;
	}
	public void setStartingAngle(float startingAngle) {
		this.startingAngle = startingAngle;
	}
	public float getEndingAngle() {
		return endingAngle;
	}
	public void setEndingAngle(float endingAngle) {
		this.endingAngle = endingAngle;
	}

	@Override
	public void valueChanged(Timeline timeline, ChangeNode changeNode,
			KeyValue changedValue) {
		
		//call to super for SRT and anchor property values
		super.valueChanged(timeline, changeNode, changedValue);
		
		//since same handler is used for all 4 properties , we will use tag to identify this callback
		switch (changeNode.tag) {
		case INNER_RADIUS_HANDLE:
			innerRadius=changedValue.getValue(0);
			break;
		case OUTER_RADIUS_HANDLE:
			outerRadius=changedValue.getValue(0);
			break;
		case STARTING_ANGLE_HANDLE:
			startingAngle=changedValue.getValue(0);
			break;
		case ENDING_ANGLE_HANDLE:
			endingAngle=changedValue.getValue(0);
			break;

		default:
			break;
		}
		notifyModelChangeListener();
	}

	@Override
	public void registerWithTimeline(Timeline timeline) {
		super.registerWithTimeline(timeline);
		KeyFrame.addAllChangeNodesToTimeline(innerRadiusStart, timeline, this, INNER_RADIUS_HANDLE);
		KeyFrame.addAllChangeNodesToTimeline(outerRadiusStart, timeline, this, OUTER_RADIUS_HANDLE);
		KeyFrame.addAllChangeNodesToTimeline(startingAngleStart, timeline, this, STARTING_ANGLE_HANDLE);
		KeyFrame.addAllChangeNodesToTimeline(endingAngleStart, timeline, this, ENDING_ANGLE_HANDLE);
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
