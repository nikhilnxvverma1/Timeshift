package com.nikhil.timeline.interpolation;

public class Linear implements InterpolationCurve{

	public static final String CURVE_NAME="Linear";
	
	@Override
	public double valueFor(float t) {
		return t;
	}

	@Override
	public String getCurveName() {
		return CURVE_NAME;
	}

}
