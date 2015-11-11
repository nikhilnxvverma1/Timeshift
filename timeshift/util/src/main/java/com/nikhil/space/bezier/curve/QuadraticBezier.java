package com.nikhil.space.bezier.curve;

import com.nikhil.space.Parametric;
import com.nikhil.util.modal.UtilPoint;

public class QuadraticBezier implements Parametric{

	private UtilPoint starting;
	private UtilPoint controlPoint;
	private UtilPoint ending;
	
	/**
	 * Creates a new Quadratic bezier curve
	 * @param p0 starting point
	 * @param p1 middle control point
	 * @param p2 ending point
	 */
	public QuadraticBezier(UtilPoint p0,UtilPoint p1,UtilPoint p2){
		this.starting=p0;
		this.controlPoint=p1;
		this.ending=p2;
	}
	
	@Override
	public UtilPoint getPointFor(double t) {
		//B(t)=(1-t)^2*P0 + 2*(1-t)*t*P1 + t^2*P2
		UtilPoint firstComponent=starting.multiply((1-t)*(1-t));
		UtilPoint secondComponent=controlPoint.multiply(2*(1-t)*t);
		UtilPoint thirdComponent=ending.multiply(t*t);
		return firstComponent.add(secondComponent).add(thirdComponent);
	}

}
