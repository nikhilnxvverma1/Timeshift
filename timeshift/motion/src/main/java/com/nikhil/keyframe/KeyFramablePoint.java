package com.nikhil.keyframe;

public class KeyFramablePoint extends SpatialKeyFrame{

	private KeyFramablePoint previousKeyFramablePoint;
	private KeyFramablePoint nextKeyFramablePoint;	

	@Override
	public KeyFrame getNextKeyFrame() {
		return nextKeyFramablePoint;
	}

	@Override
	public KeyFrame getPreviousKeyFrame() {
		return previousKeyFramablePoint;
	}

	public void setPreviousKeyFramablePoint(
			KeyFramablePoint previousKeyFramablePoint) {
		this.previousKeyFramablePoint = previousKeyFramablePoint;
	}

	public void setNextKeyFramablePoint(KeyFramablePoint nextKeyFramablePoint) {
		this.nextKeyFramablePoint = nextKeyFramablePoint;
	}

}
