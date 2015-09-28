package com.nikhil.model.pathsystem.rupture;

public class PathValidityNode {

	private float distance;
	private boolean pathWithNextValid;
	private PathValidityNode previous,next;
	
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public boolean isPathWithNextValid() {
		return pathWithNextValid;
	}
	public void setPathWithNextValid(boolean pathWithNextValid) {
		this.pathWithNextValid = pathWithNextValid;
	}
	public PathValidityNode getPrevious() {
		return previous;
	}
	public void setPrevious(PathValidityNode previous) {
		this.previous = previous;
	}
	public PathValidityNode getNext() {
		return next;
	}
	public void setNext(PathValidityNode next) {
		this.next = next;
	}
	
}
