package com.nikhil.model.pathsystem;

import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.model.ModelVisitor;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.fromto.FromToChangeHandler;

/**
 * Just like a bezier point but the control points are relative to the anchor point
 * @author Nikhil Verma
 *
 */
public class MovableLinkPoint extends LinkPoint implements FromToChangeHandler {


	private SpatialKeyframe controlPointWithPreviousStart;

	private SpatialKeyframe anchorPointStart;

	private SpatialKeyframe controlPointWithNextStart;

	public SpatialKeyframe getControlPointWithPreviousStart() {
		return controlPointWithPreviousStart;
	}

	public void setControlPointWithPreviousStart(SpatialKeyframe controlPointWithPreviousStart) {
		this.controlPointWithPreviousStart = controlPointWithPreviousStart;
	}

	public SpatialKeyframe getAnchorPointStart() {
		return anchorPointStart;
	}

	public void setAnchorPointStart(SpatialKeyframe anchorPointStart) {
		this.anchorPointStart = anchorPointStart;
	}

	public SpatialKeyframe getControlPointWithNextStart() {
		return controlPointWithNextStart;
	}

	public void setControlPointWithNextStart(SpatialKeyframe controlPointWithNextStart) {
		this.controlPointWithNextStart = controlPointWithNextStart;
	}

	@Override
	public void valueChanged(double currentTime,  ChangeNode changeNode,
			KeyValue changedValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void acceptVisitor(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
