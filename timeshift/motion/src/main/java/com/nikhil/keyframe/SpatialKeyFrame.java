package com.nikhil.keyframe;

import com.nikhil.space.Parametric;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.SpatialChangeNode;
import com.nikhil.timeline.changehandler.ChangeHandler;

public abstract class SpatialKeyFrame extends KeyFrame{

	protected Parametric spatialValues;
	
	@Override
	protected ChangeNode buildChangeNode(ChangeHandler changeHandler, int tag) {
		
		KeyFrame keyframeAfterThis = (SpatialKeyFrame)findClosestAfter();
		SpatialChangeNode changeNodeBetweenThisAndNext=new SpatialChangeNode(changeHandler, 
				interpolationWithAfter,
				time,
				keyframeAfterThis.time-time,
				null,
				null,
				spatialValues);
		changeNodeBetweenThisAndNext.tag=tag;
		return changeNodeBetweenThisAndNext;
	}
	
}
