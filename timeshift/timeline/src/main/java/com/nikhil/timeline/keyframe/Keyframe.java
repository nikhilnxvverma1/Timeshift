package com.nikhil.timeline.keyframe;

import com.nikhil.math.MathUtil;
import com.nikhil.timeline.interpolation.*;

/**
 * Keyframe denotes the value of a property in time.
 * Subclasses organize keyframes as ordered linked list
 * where the interpolation with next is maintained in this class.
 */
public abstract class Keyframe {

	public static final Linear LINEAR=new Linear();
	public static final EaseIn EASE_IN=new EaseIn();
	public static final EaseOut EASE_OUT=new EaseOut();
	public static final EaseInOut EASE_IN_OUT=new EaseInOut();

	protected double time;
	protected InterpolationModel interpolationWithNext=new InterpolationModel();

	public Keyframe(double time) {
		this.time = time;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * Interpolation that this keyframe has with next,Default is linear
	 * @return an interpolation curve, used during animation
	 */
	public InterpolationCurve getInterpolationWithNext() {
		return interpolationWithNext;
	}

	public void setInterpolationWithNext(InterpolationModel interpolationWithNext) {
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

	public abstract Keyframe getNext();
	public abstract Keyframe getPrevious();

}
