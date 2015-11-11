package com.nikhil.model.freeform;

import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;
import com.nikhil.util.modal.UtilPoint;

/**
 * Holds a point that can be moved (and keyframed) independently.
 * Additionally this point also has "next" property which allows
 * it to be connected to several other Movable points in linked list fashion.
 * Created by NikhilVerma on 21/08/15.
 */
public class MovablePoint implements ModelElement,Subject{

	private SpatialKeyframeChangeNode positionChange =new SpatialKeyframeChangeNode();

	private MovablePoint next;

	protected Observer observer;

	public UtilPoint getPoint() {
		return positionChange.getCurrentPoint();
	}

	public void setPoint(UtilPoint point) {
		positionChange.getCurrentPoint().set(point);
	}

	public MovablePoint getNext() {
		return next;
	}

	public void setNext(MovablePoint next) {
		this.next = next;
	}

	public Observer getObserver() {
		return observer;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	
	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void notifyModelChangeListener() {
		observer.update(this);
	}

	public SpatialKeyframeChangeNode positionChange(){
		return positionChange;
	}

}
