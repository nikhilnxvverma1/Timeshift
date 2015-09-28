package com.nikhil.timeline.interpolation;

/**
 * Strategy that defines the value of y for an increasing value of 't'
 * where 't' stands for time.
 * @author Nikhil Verma
 *
 */
public interface InterpolationCurve {

	/**
	 * value of 'y'(around 0.0 to 1.0) for specified value of t which stands for time.
	 * 'y' should be between 0.0 to 1.0 but in some cases it can also cross these limits.
	 * For example an elastic curve where 'y' could overshoot or undershoot these limits. 
	 * @param t time, this will always be between 0.0 to 1.0
	 * @return value of y as 't' goes right.
	 */
	public double valueFor(float t);
	
	/**
	 * User friendly name of the curve. This has nothing to do with the computation
	 * @return name of the curve example : Linear, Bezier ,etc.	
	 */
	public String getCurveName();
	
}
