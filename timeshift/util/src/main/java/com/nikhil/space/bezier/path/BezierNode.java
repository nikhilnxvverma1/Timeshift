package com.nikhil.space.bezier.path;

public interface BezierNode {
	public BezierPoint getBezierPoint();
	public BezierNode getNextBezierNode();
	public BezierNode getPreviousBezierNode();
}
