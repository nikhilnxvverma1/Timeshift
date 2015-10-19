package com.nikhil.model.shape;

import com.nikhil.common.Observer;
import com.nikhil.common.Subject;
import com.nikhil.keyframe.KeyFramablePoint;
import com.nikhil.keyframe.KeyFrame;
import com.nikhil.keyframe.TemporalKeyFrame;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelElement;
import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;
import com.nikhil.timeline.changehandler.ChangeHandler;
import com.nikhil.util.modal.UtilPoint;

/**
 * Abstract shape class defines scale,rotation,translation,anchor point,color and opacity.
 * TODO make contains and overlaps method
 */
public abstract class ShapeModel extends ItemModel implements ChangeHandler,Subject,ModelElement {

	public static final int SCALE_CHANGE_TAG = -2;
	public static final int ROTATION_CHANGE_TAG = -3;
	public static final int TRANSLATION_CHANGE_TAG = -4;
	public static final int ANCHOR_POINT_CHANGE_TAG = -5;

	public static final double DEFAULT_SCALE=1;
	public static final double DEFAULT_ROTATION=0;
	public static final double DEFAULT_TRANSLATION_X=0;
	public static final double DEFAULT_TRANSLATION_Y=0;

	/** General purpose tag that can be used by client to identify this shape among others */
	public int tag;
	
	protected float scale=(float)DEFAULT_SCALE;//TODO change type to double
	private TemporalKeyFrame scaleStart;
	protected float rotation=(float)DEFAULT_ROTATION;
	private TemporalKeyFrame rotationStart;
	protected UtilPoint translation=new UtilPoint(DEFAULT_TRANSLATION_X,DEFAULT_TRANSLATION_Y);
	private KeyFramablePoint translationStart;

	protected UtilPoint anchorPoint=new UtilPoint();
	private KeyFramablePoint anchorPointStart;
	
	protected Observer observer;
	
	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public UtilPoint getTranslation() {
		return translation;
	}

	public void setTranslation(UtilPoint translation) {
		this.translation = translation;
	}

	public void setTranslation(double x,double y){
		this.translation.set(x,y);
	}

	public UtilPoint getAnchorPoint() {
		return anchorPoint;
	}

	public void setAnchorPoint(UtilPoint anchorPoint) {
		this.anchorPoint = anchorPoint;
	}

	public void setAnchorPoint(double x,double y){
		this.anchorPoint.set(x,y);
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

		switch (changeNode.tag) {
		case SCALE_CHANGE_TAG:
			scale = changedValue.getValue(0);
			break;
		case ROTATION_CHANGE_TAG:
			rotation = changedValue.getValue(0);
			break;
		case TRANSLATION_CHANGE_TAG:
			// TODO set changed translation
			break;
		case ANCHOR_POINT_CHANGE_TAG:
			// TODO set changed anchor point
			break;

		default:
			break;
		}

	}

	@Override
	public void valueAtEnd(Timeline timeline, ChangeNode changeNode) {
		// TODO Auto-generated method stub

	}

	public void registerWithTimeline(Timeline timeline) {
		// register all keyframes
		
		KeyFrame.addAllChangeNodesToTimeline(scaleStart, timeline, this, SCALE_CHANGE_TAG);
		KeyFrame.addAllChangeNodesToTimeline(rotationStart, timeline, this, ROTATION_CHANGE_TAG);
		KeyFrame.addAllChangeNodesToTimeline(translationStart, timeline, this, TRANSLATION_CHANGE_TAG);
		KeyFrame.addAllChangeNodesToTimeline(anchorPointStart, timeline, this, ANCHOR_POINT_CHANGE_TAG);
		
	}

	@Override
	public void notifyModelChangeListener() {
		if (observer != null) {
			observer.update(this);
		}
	}

}
