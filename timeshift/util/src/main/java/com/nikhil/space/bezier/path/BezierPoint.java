package com.nikhil.space.bezier.path;

import com.nikhil.util.modal.UtilPoint;

public class BezierPoint {

	protected UtilPoint anchorPoint;
	protected UtilPoint controlPointWithPrevious;
	protected UtilPoint controlPointWithNext;

	/**
	 * Returns the interpolated point between two bezier points
	 * @param p1 the first bezier point
	 * @param p2 the second bezier point
	 * @param t parameter between 0.0 to 1.0
	 * @return interpolated point between two bezier points
	 */
	public static UtilPoint getInterpolatedPointBetween(BezierPoint p1,BezierPoint p2,double t){

		//B(t)=(1-t)^3*P0 + 3*(1-t)^2*t*P1 + 3*(1-t)*t^2*P2 + t^3*P3
		UtilPoint firstComponent=p1.anchorPoint.multiply((1-t)*(1-t)*(1-t));

		//control points are relative to the anchor points,therefore, we must add anchor points to them
		UtilPoint secondComponent=p1.anchorPoint.add(p1.controlPointWithNext).multiply(3 * (1 - t) * (1 - t) * t);
		UtilPoint thirdComponent=p2.anchorPoint.add(p2.controlPointWithPrevious).multiply(3*(1-t)*t*t);
		UtilPoint fourthComponent=p2.anchorPoint.multiply(t*t*t);
		return firstComponent.add(secondComponent).add(thirdComponent).add(fourthComponent);
	}

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
	 * Deep copies the content of another bezier point into this bezier point
	 * @param copyFrom the point to copy from
	 */
	public void set(BezierPoint copyFrom){
		anchorPoint.set(copyFrom.anchorPoint);
		controlPointWithNext.set(copyFrom.controlPointWithNext);
		controlPointWithPrevious.set(copyFrom.controlPointWithPrevious);
	}
}
