package com.nikhil.timeline.keyframe;

import com.nikhil.space.Parametric;
import com.nikhil.util.modal.UtilPoint;

/**
 * Spatial keyframes allow changes of a value in 2d space(eg: translation property) defined by a cubic bezier curve.
 * These keyframes are put in a ordered linked list where each node is a bezier point.
 */
public class SpatialKeyframe extends Keyframe implements Parametric{

	private UtilPoint anchorPoint;
	private UtilPoint controlPointWithNext;
	private SpatialKeyframe previous;
	private SpatialKeyframe next;

	/**
	 * Creates a spatial keyframe with no previous or next
	 * @param time time at which the value should be the anchor point
	 * @param anchorPoint the starting point of the bezier curve with next
	 */
	public SpatialKeyframe(double time,UtilPoint anchorPoint) {
		this(time,null,anchorPoint,null);
	}

	/**
	 * Creates a spatial keyframe with a previous but no next
	 * @param time time at which the value should be the anchor point
	 * @param previous previous spatial keyframe for which this one is the end
	 * @param anchorPoint the starting point of the bezier curve with next
	 */
	public SpatialKeyframe(double time,SpatialKeyframe previous, UtilPoint anchorPoint) {
		this(time,previous,anchorPoint,null);
	}

	/**
	 * Creates a spatial keyframe with a next but no previous
	 * @param time time at which the value should be the anchor point
	 * @param anchorPoint the starting point of the bezier curve with next
	 * @param next next spatial keyframe containing the end point of this bezier curve
	 */
	public SpatialKeyframe(double time,UtilPoint anchorPoint, SpatialKeyframe next) {
		this(time,null,anchorPoint,next);
	}

	/**
	 * Creates a spatial keyframe with a next but no previous
	 * @param time time at which the value should be the anchor point
	 * @param previous previous spatial keyframe for which this one is the end
	 * @param anchorPoint the starting point of the bezier curve with next
	 * @param next next spatial keyframe containing the end point of this bezier curve
	 */
	public SpatialKeyframe(double time,SpatialKeyframe previous, UtilPoint anchorPoint, SpatialKeyframe next) {
		super(time);
		this.previous = previous;
		this.anchorPoint = anchorPoint;
		this.next = next;
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

	public SpatialKeyframe getPrevious() {
		return previous;
	}

	public void setPrevious(SpatialKeyframe previous) {
		this.previous = previous;
	}

	public SpatialKeyframe getNext() {
		return next;
	}

	public void setNext(SpatialKeyframe next) {
		this.next = next;
	}

	@Override
	public UtilPoint getPointFor(double t) {
		//TODO work with dependency on next
		return null;
	}
}
