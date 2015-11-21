package com.nikhil.timeline.keyframe;

import com.nikhil.math.MathUtil;
import com.nikhil.timeline.interpolation.InterpolationCurve;

/**
 * Keyframe denotes the value of a property in time.
 * Subclasses organize keyframes as ordered linked list
 * where the interpolation with next is defined in this class
 */
public abstract class Keyframe {
	
	protected double time;
	protected InterpolationCurve interpolationWithNext;

	public Keyframe(double time) {
		this.time = time;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public InterpolationCurve getInterpolationWithNext() {
		return interpolationWithNext;
	}
	public void setInterpolationWithNext(InterpolationCurve interpolationWithNext) {
		this.interpolationWithNext = interpolationWithNext;
	}

	/**
	 * checks if this keyframe lies within a margin around the specified time
	 * @param time the time around which this keyframe is
	 * @param margin the acceptable time difference between specified time
	 *                  and "this" keyframe's time
	 * @return true if it lies within marin, false otherwise
	 */
	public boolean withinMargin(double time,double margin ){
		return MathUtil.abs(time-this.time)<=margin;
	}
}
