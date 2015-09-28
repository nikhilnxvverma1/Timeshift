package com.nikhil.space.bezier.path;

import com.nikhil.util.modal.UtilPoint;

public class BezierPath{

	/**
	 * location of the bezier path in space,
	 * everything is relative to this point 
	 */
	private UtilPoint location;
	private BezierNode start,last;

	public BezierPath(){
		super();
	}
	
	/**
	 * Creates a linear bezier path
	 * @param startingPoint starting point of line
	 * @param finishingPoint ending point of line
	 */
	public BezierPath(UtilPoint startingPoint,UtilPoint finishingPoint){
		SimpleBezierNode linearStart=new SimpleBezierNode(startingPoint,new UtilPoint(0,0),new UtilPoint(0,0));
		SimpleBezierNode linearEnd=new SimpleBezierNode(finishingPoint,new UtilPoint(0,0),new UtilPoint(0,0));
		linearStart.setNextBezierNode(linearEnd);
		linearEnd.setPreviousBezierNode(linearStart);
		location=new UtilPoint(0,0);
		start=linearStart;
		last=linearEnd;
	}
	
	public UtilPoint getLocation() {
		return location;
	}

	public void setLocation(UtilPoint location) {
		this.location = location;
	}

	public BezierNode getStart() {
		return start;
	}

	public void setStart(BezierNode start) {
		this.start = start;
	}

	public BezierNode getLast() {
		return last;
	}

	public void setLast(BezierNode last) {
		this.last = last;
	}	
	
}
