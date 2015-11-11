package com.nikhil.timeline.interpolation;

import com.nikhil.space.bezier.curve.QuadraticBezier;
import com.nikhil.util.modal.UtilPoint;

public class EaseIn implements InterpolationCurve{

	public static final String CURVE_NAME="Ease In";
	
	private QuadraticBezier bezierCurve;
	
	/**
	 * Creates a new Ease In Strategy which has full bend  
	 */
	public EaseIn(){
		this(1.0f);
	}
	
	/**
	 * Creates a new Ease In Strategy 
	 * @param bend between 0.0 to 1.0 that defines how much bend the curve will have
	 */
	public EaseIn(float bend){
		UtilPoint starting=new UtilPoint(0,0);
		UtilPoint ending=new UtilPoint(1,1);
		UtilPoint controlPoint=new UtilPoint(0.5f+bend*0.5f,0.5f-bend*0.5f);
		bezierCurve=new QuadraticBezier(starting, controlPoint, ending);
	}
	
	@Override
	public double valueFor(double t) {
		return bezierCurve.getPointFor(t).getY();
	}

	public String getCurveName() {
		return CURVE_NAME;
	}

}
