package com.nikhil.space.bezier.path;

import com.nikhil.util.modal.UtilPoint;

public class BezierPoint {

	private UtilPoint anchorPoint;
	private UtilPoint controlPointWithPrevious;
	private UtilPoint controlPointWithNext;

	public BezierPoint(UtilPoint anchorPoint){
		this(anchorPoint,new UtilPoint(0,0),new UtilPoint(0,0));
	}
	
	public BezierPoint(UtilPoint anchorPoint, UtilPoint controlPointWithPrevious,
			UtilPoint controlPointWithNext) {
		super();
		this.anchorPoint = anchorPoint;
		this.controlPointWithPrevious = controlPointWithPrevious;
		this.controlPointWithNext = controlPointWithNext;
	}

	/**
	 * Copy constructor
	 * @param bezierPoint the bezier point to copy values from
	 */
	public BezierPoint(BezierPoint bezierPoint) {
		this(new UtilPoint(0,0));//call the other constructor so that points are instantiated
		this.set(bezierPoint);
	}

	public UtilPoint getControlPointWithPrevious() {
		return controlPointWithPrevious;
	}
	public void setControlPointWithPrevious(UtilPoint controlPointWithPrevious) {
		this.controlPointWithPrevious = controlPointWithPrevious;
	}
	public UtilPoint getAnchorPoint() {
		return anchorPoint;
	}
	public void setAnchorPoint(UtilPoint anchorPoint) {
		this.anchorPoint = anchorPoint;
	}
	public UtilPoint getControlPointWithNext() {
		return controlPointWithNext;
	}
	public void setControlPointWithNext(UtilPoint controlPointWithNext) {
		this.controlPointWithNext = controlPointWithNext;
	}

	/**
	 * Copies the content of another bezier point into this bezier point
	 * @param copyFrom the point to copy from
	 */
	public void set(BezierPoint copyFrom){
		anchorPoint.set(copyFrom.anchorPoint);
		controlPointWithNext.set(copyFrom.controlPointWithNext);
		controlPointWithPrevious.set(copyFrom.controlPointWithPrevious);
	}
}
