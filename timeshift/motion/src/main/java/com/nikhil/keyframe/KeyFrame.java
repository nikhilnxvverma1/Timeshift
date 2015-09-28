package com.nikhil.keyframe;

import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.Timeline;
import com.nikhil.timeline.changehandler.ChangeHandler;
import com.nikhil.timeline.interpolation.InterpolationCurve;

public abstract class KeyFrame {
	
	protected float time;
	protected InterpolationCurve interpolationWithAfter;
	
	private ChangeNode changeNode;//only used transactionally(not a part of modal)
	
	public float getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	public InterpolationCurve getInterpolationWithAfter() {
		return interpolationWithAfter;
	}
	public void setInterpolationWithAfter(InterpolationCurve interpolationWithAfter) {
		this.interpolationWithAfter = interpolationWithAfter;
	}
	
	protected abstract ChangeNode buildChangeNode(ChangeHandler changeHandler,int tag);
	
	public abstract KeyFrame getNextKeyFrame();
	
	public abstract KeyFrame getPreviousKeyFrame();
	
	public final void addChangeNodeToTimeline(Timeline timeline,ChangeHandler changeHandler,int tag){
		
		ChangeNode changeNodeBetweenThisAndNext=buildChangeNode(changeHandler, tag);
		//add this to the timeline
		this.changeNode=changeNodeBetweenThisAndNext;
		timeline.appendChangeNodeToList(changeNodeBetweenThisAndNext);
	}
	
	public ChangeNode getChangeNode() {
		return changeNode;
	}
	
	public static void addAllChangeNodesToTimeline(KeyFrame start,Timeline timeline,ChangeHandler changeHandler,int tag){		
		KeyFrame t=start;
		while(t!=null){
			t.addChangeNodeToTimeline(timeline, changeHandler, tag);
			t=t.getNextKeyFrame();
		}
	}
	
	/**
	 * @return finds the keyframe with a time value right after this keyframe's
	 */
	public KeyFrame findClosestAfter(){
		float closestTimeAfter=99999;
		KeyFrame closestAfter=null;
		//find the closest time in the list before (excluding header node)
		KeyFrame t=getPreviousKeyFrame();
		while(t!=null){
			if((t.time>=time)&&(t.time<=closestTimeAfter)){
				closestTimeAfter=t.time;
				closestAfter=t;
			}
			t=t.getPreviousKeyFrame();
		}
		//find the closest time in the list after
		t=t.getNextKeyFrame();
		while(t!=null){
			if((t.time>=time)&&(t.time<=closestTimeAfter)){
				closestTimeAfter=t.time;
				closestAfter=t;
			}
			t=t.getNextKeyFrame();
		}
		return closestAfter;
	}
	
}
