package com.nikhil.space.bezier.path;

import com.nikhil.util.modal.UtilPoint;

public class SimpleBezierNode implements BezierNode{

	private BezierPoint bezierPoint;
	private SimpleBezierNode previousBezierNode;
	private SimpleBezierNode nextBezierNode;
	
	public SimpleBezierNode(UtilPoint controlPoint,UtilPoint anchorWithPrevious,UtilPoint anchorWithNext) {
		bezierPoint=new BezierPoint(controlPoint,anchorWithPrevious,anchorWithNext);
	}
	
	@Override
	public BezierPoint getBezierPoint() {
		return bezierPoint;
	}

	@Override
	public BezierNode getNextBezierNode() {
		return nextBezierNode;
	}

	@Override
	public BezierNode getPreviousBezierNode() {
		return previousBezierNode;
	}

	public void setPreviousBezierNode(SimpleBezierNode previousBezierNode) {
		this.previousBezierNode = previousBezierNode;
	}

	public void setNextBezierNode(SimpleBezierNode nextBezierNode) {
		this.nextBezierNode = nextBezierNode;
	}

	public void setBezierPoint(BezierPoint bezierPoint) {
		this.bezierPoint = bezierPoint;
	}

}
