package com.nikhil.keyframe;

import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.changehandler.ChangeHandler;

public class TemporalKeyFrame extends KeyFrame{

	private KeyValue keyValue;
	
	private TemporalKeyFrame nextKeyFrame;
	private TemporalKeyFrame previousKeyFrame;
	
	public TemporalKeyFrame(KeyValue keyValue) {
		this.keyValue=keyValue;
	}
	
	@Override
	protected ChangeNode buildChangeNode(ChangeHandler changeHandler, int tag) {
		TemporalKeyFrame keyframeAfterThis=(TemporalKeyFrame)findClosestAfter();
		ChangeNode changeNodeBetweenThisAndNext=new ChangeNode(changeHandler, 
				interpolationWithAfter,
				time,
				keyframeAfterThis.time-time,
				keyValue,
				keyframeAfterThis.keyValue);
		changeNodeBetweenThisAndNext.tag=tag;
		return changeNodeBetweenThisAndNext;
	}

	@Override
	public KeyFrame getNextKeyFrame() {
		return nextKeyFrame;
	}

	@Override
	public KeyFrame getPreviousKeyFrame() {
		return previousKeyFrame;
	}
	
	public void setNextKeyFrame(TemporalKeyFrame nextKeyFrame){
		this.nextKeyFrame=nextKeyFrame;
	}

	public void setPreviousKeyFrame(TemporalKeyFrame previousKeyFrame) {
		this.previousKeyFrame = previousKeyFrame;
	}

}
