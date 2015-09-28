package com.nikhil.model.freeform;

import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.keyframe.KeyFramablePoint;
import com.nikhil.keyframe.KeyFrame;
import com.nikhil.model.ModelElement;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;
import com.nikhil.timeline.changehandler.ChangeHandler;
import com.nikhil.util.modal.UtilPoint;

/**
 * Holds a point that can be moved (and keyframed) independently.
 * Additionally this point also has "next" property which allows
 * it to be connected to several other Movable points in linked list fashion.
 * Created by NikhilVerma on 21/08/15.
 */
public class MovablePoint implements ChangeHandler,ModelElement,Subject{

	public static final int POINT_HANDLE=1;

	private UtilPoint point;
	private KeyFramablePoint pointStart;

	private MovablePoint next;

	protected Observer observer;

	public UtilPoint getPoint() {
		return point;
	}
	public void setPoint(UtilPoint point) {
		this.point = point;
	}

	public KeyFramablePoint getPointStart() {
		return pointStart;
	}

	public void setPointStart(KeyFramablePoint pointStart) {
		this.pointStart = pointStart;
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
	public void valueAtStart(Timeline timeline, ChangeNode changeNode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueChanged(Timeline timeline, ChangeNode changeNode,
			KeyValue changedValue) {
		switch (changeNode.tag){
			case POINT_HANDLE:
				point.set(changedValue.getValue(0),changedValue.getValue(1));
				break;
		}
		notifyModelChangeListener();
	}
	@Override
	public void valueAtEnd(Timeline timeline, ChangeNode changeNode) {
		// TODO Auto-generated method stub

	}


	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void notifyModelChangeListener() {
		observer.update(this);
	}

	public void registerWithTimeline(Timeline timeline) {
		KeyFrame.addAllChangeNodesToTimeline(pointStart,timeline,this,POINT_HANDLE);
	}
}
