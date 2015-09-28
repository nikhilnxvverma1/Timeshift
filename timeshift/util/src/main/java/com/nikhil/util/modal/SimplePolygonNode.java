package com.nikhil.util.modal;


public class SimplePolygonNode implements PolygonNode{

	private UtilPoint point;
	private PolygonNode nextPolygonNode,previousPolygonNode;
	
	@Override
	public UtilPoint getPolygonPoint() {
		return point;
	}

	@Override
	public PolygonNode getNextPolygonNode() {
		return nextPolygonNode;
	}

	@Override
	public PolygonNode getPreviousPolygonNode() {
		return previousPolygonNode;
	}

}
