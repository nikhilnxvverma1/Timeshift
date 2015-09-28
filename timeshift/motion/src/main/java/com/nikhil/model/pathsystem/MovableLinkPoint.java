package com.nikhil.model.pathsystem;

import com.nikhil.keyframe.KeyFramablePoint;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;
import com.nikhil.timeline.changehandler.ChangeHandler;

/**
 * Just like a bezier point but the control points are relative to the anchor point
 * @author Nikhil Verma
 *
 */
public class MovableLinkPoint extends LinkPoint implements ChangeHandler{


	private KeyFramablePoint controlPointWithPreviousStart;

	private KeyFramablePoint anchorPointStart;

	private KeyFramablePoint controlPointWithNextStart;

	public KeyFramablePoint getControlPointWithPreviousStart() {
		return controlPointWithPreviousStart;
	}

	public void setControlPointWithPreviousStart(KeyFramablePoint controlPointWithPreviousStart) {
		this.controlPointWithPreviousStart = controlPointWithPreviousStart;
	}

	public KeyFramablePoint getAnchorPointStart() {
		return anchorPointStart;
	}

	public void setAnchorPointStart(KeyFramablePoint anchorPointStart) {
		this.anchorPointStart = anchorPointStart;
	}

	public KeyFramablePoint getControlPointWithNextStart() {
		return controlPointWithNextStart;
	}

	public void setControlPointWithNextStart(KeyFramablePoint controlPointWithNextStart) {
		this.controlPointWithNextStart = controlPointWithNextStart;
	}

	@Override
	public void valueAtStart(Timeline timeline, ChangeNode changeNode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueChanged(Timeline timeline, ChangeNode changeNode,
			KeyValue changedValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueAtEnd(Timeline timeline, ChangeNode changeNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
