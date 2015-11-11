package com.nikhil.timeline.interpolation;

import com.nikhil.space.bezier.curve.CubicBezier;
import com.nikhil.util.modal.UtilPoint;

public class EaseInOut implements InterpolationCurve{

	public static final String CURVE_NAME="Ease In Out";

	private CubicBezier bezierCurve;
	
	public EaseInOut(){
		this(1,1);
	}
	
	public EaseInOut(float easeInBend,float easeOutBend){
		UtilPoint starting=new UtilPoint(0,0);
		UtilPoint ending=new UtilPoint(1,1);
		UtilPoint easeInControlPoint=new UtilPoint(easeInBend,0);
		UtilPoint easeOutControlPoint=new UtilPoint(1.0f-easeOutBend,1);
		bezierCurve=new CubicBezier(starting, easeInControlPoint, easeOutControlPoint, ending);
	}
	
	@Override
	public double valueFor(double t) {
		return bezierCurve.getPointFor(t).getY();
	}

	public String getCurveName() {
		return CURVE_NAME;
	}

}
