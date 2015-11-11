package com.nikhil.timeline.interpolation;

public class Linear implements InterpolationCurve{

	public static final String CURVE_NAME="Linear";
	
	@Override
	public double valueFor(double t) {
		return t;
	}

	public String getCurveName() {
		return CURVE_NAME;
	}

}
