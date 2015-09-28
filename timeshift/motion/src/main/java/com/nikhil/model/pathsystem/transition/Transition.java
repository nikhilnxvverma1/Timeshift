package com.nikhil.model.pathsystem.transition;

import com.nikhil.model.pathsystem.TravelPath;

public abstract class Transition {

	protected TravelPath fromPath;
	protected TravelPath toPath;
	private Transition next;

	public TravelPath getFromPath() {
		return fromPath;
	}

	public void setFromPath(TravelPath fromPath) {
		this.fromPath = fromPath;
	}

	public TravelPath getToPath() {
		return toPath;
	}

	public void setToPath(TravelPath toPath) {
		this.toPath = toPath;
	}

	public Transition getNext() {
		return next;
	}

	public void setNext(Transition next) {
		this.next = next;
	}

	public abstract TransitionType getTransitionType();
}
