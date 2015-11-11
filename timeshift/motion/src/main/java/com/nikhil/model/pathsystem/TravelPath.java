package com.nikhil.model.pathsystem;

import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;
import com.nikhil.model.pathsystem.rupture.PathValidityNode;
import com.nikhil.model.pathsystem.transition.Transition;
import com.nikhil.space.Parametric;
import com.nikhil.util.modal.UtilPoint;

public class TravelPath implements Parametric,ModelElement {

	private float duration;
	private PathValidityNode validityStart,validityLast;

	private LinkPoint startingPoint;
	private LinkPoint endingPoint;

	private Transition transitionStart;

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public PathValidityNode getValidityStart() {
		return validityStart;
	}

	public void setValidityStart(PathValidityNode validityStart) {
		this.validityStart = validityStart;
	}

	public PathValidityNode getValidityLast() {
		return validityLast;
	}

	public void setValidityLast(PathValidityNode validityLast) {
		this.validityLast = validityLast;
	}

	public LinkPoint getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(LinkPoint startingPoint) {
		this.startingPoint = startingPoint;
	}

	public LinkPoint getEndingPoint() {
		return endingPoint;
	}

	public void setEndingPoint(LinkPoint endingPoint) {
		this.endingPoint = endingPoint;
	}

	public Transition getTransitionStart() {
		return transitionStart;
	}

	public void setTransitionStart(Transition transitionStart) {
		this.transitionStart = transitionStart;
	}

	public void addTransition(Transition transitionToAdd){
		//order doesn't matter ,so simply prepend this transtion to the list
		//takes constant time
		transitionToAdd.setNext(transitionStart);
		transitionStart=transitionToAdd;
	}

	public Transition removeTransition(Transition transitionToRemove){
		Transition previous=null;
		Transition t=transitionStart;
		while (t != null) {

			//on finding transition
			if (t == transitionToRemove) {

				//remove transition by omitting this transition from the next of previous
				if (previous == null) {
					//this transition is the starting transition
					transitionStart=t.getNext();
				}else{
					//something in the middle or perhaps end
					previous.setNext(t.getNext());
				}
				return transitionToRemove;
			}
			previous = t;
			t = t.getNext();
		}
		return null;
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public UtilPoint getPointFor(double t) {
		return null;
	}
}
