package com.nikhil.space.bezier.curve;

import com.nikhil.space.Parametric;
import com.nikhil.util.modal.UtilPoint;

public class LinearBezier implements Parametric{

	private UtilPoint starting;
	private UtilPoint ending;
	/** Holds the difference between starting and ending. Only used to prevent making unnecessary copies */
	private UtilPoint difference;
	
	/**
	 * Creates a new linear bezier curve
	 * @param p0 starting point
	 * @param p1 ending point
	 */
	public LinearBezier(UtilPoint p0,UtilPoint p1){
		this.starting=p0;
		this.ending=p1;
		difference=ending.subtract(starting);
	}
	
	@Override
	public UtilPoint getPointFor(double t) {
		//B(t)=P0 + t*(P1-P0)
		return starting.add(difference.multiply(t));
	}

	
}
