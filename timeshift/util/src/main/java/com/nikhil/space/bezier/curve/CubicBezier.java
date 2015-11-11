package com.nikhil.space.bezier.curve;

import com.nikhil.space.Parametric;
import com.nikhil.util.modal.UtilPoint;

public class CubicBezier implements Parametric{

	protected UtilPoint starting;
	protected UtilPoint controlPoint1;
	protected UtilPoint controlPoint2;
	protected UtilPoint ending;
	
	/**
	 * Creates a new Cubic bezier curve that goes from starting to ending using two control points 
	 * @param starting the starting value
	 * @param controlPoint1 control point at starting coordinate
	 * @param controlPoint2 control point at ending coordinate
	 * @param ending the ending value
	 */
	public CubicBezier(UtilPoint starting, UtilPoint controlPoint1,
			UtilPoint controlPoint2, UtilPoint ending) {
		super();
		this.starting = starting;
		this.controlPoint1 = controlPoint1;
		this.controlPoint2 = controlPoint2;
		this.ending = ending;
	}

	@Override
	public UtilPoint getPointFor(double t) {
		
		//B(t)=(1-t)^3*P0 + 3*(1-t)^2*t*P1 + 3*(1-t)*t^2*P2 + t^3*P3
		UtilPoint firstComponenet=starting.multiply((1-t)*(1-t)*(1-t));
		UtilPoint secondComponenet=controlPoint1.multiply(3*(1-t)*(1-t)*t);
		UtilPoint thirdComponenet=controlPoint2.multiply(3*(1-t)*t*t);
		UtilPoint fourthComponenet=ending.multiply(t*t*t);
		return firstComponenet.add(secondComponenet).add(thirdComponenet).add(fourthComponenet);
	}
	
}
