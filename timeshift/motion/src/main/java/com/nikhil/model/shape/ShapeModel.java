package com.nikhil.model.shape;

import com.nikhil.common.Subject;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.change.ChangeNodeIterator;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.model.ItemModel;
import com.nikhil.model.ModelElement;
import com.nikhil.timeline.KeyValue;
import com.nikhil.util.modal.UtilPoint;

/**
 * Abstract shape class defines scale,rotation,translation,anchor point,color and opacity.
 */
public abstract class ShapeModel extends ItemModel implements Subject,ModelElement {

	public static final double DEFAULT_SCALE=1;
	public static final double DEFAULT_ROTATION=0;
	public static final double DEFAULT_TRANSLATION_X=0;
	public static final double DEFAULT_TRANSLATION_Y=0;
	
	protected TemporalKeyframeChangeNode scaleChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_SCALE));
	protected TemporalKeyframeChangeNode rotationChange=new TemporalKeyframeChangeNode(new KeyValue(DEFAULT_ROTATION));
	protected SpatialKeyframeChangeNode translationChange=new SpatialKeyframeChangeNode();
	protected SpatialKeyframeChangeNode anchorPointChange=new SpatialKeyframeChangeNode();

	public void setScale(double scale){
		scaleChange.getCurrentValue().set(0,scale);
	}
	public double getScale(){
		return scaleChange.getCurrentValue().get(0);
	}
	public void setRotation(double rotation){
		rotationChange.getCurrentValue().set(0, rotation);
	}

	public double getRotation(){
		return rotationChange.getCurrentValue().get(0);
	}

	public void setTranslation(UtilPoint p){
		this.setTranslation(p.getX(), p.getY());
	}
	public void setTranslation(double x,double y){
		translationChange.setCurrentPoint(x,y);
	}
	public UtilPoint getTranslation(){
		return translationChange.getCurrentPoint();
	}

	public void setAnchorPoint(UtilPoint p){
		this.setAnchorPoint(p.getX(), p.getY());
	}
	public void setAnchorPoint(double x,double y){
		anchorPointChange.setCurrentPoint(x,y);
	}
	public UtilPoint getAnchorPoint(){
		return anchorPointChange.getCurrentPoint();
	}

	public TemporalKeyframeChangeNode scaleChange(){
		return scaleChange;
	}

	public TemporalKeyframeChangeNode rotationChange(){
		return rotationChange;
	}
	public SpatialKeyframeChangeNode translationChange(){
		return translationChange;
	}
	public SpatialKeyframeChangeNode anchorPointChange(){
		return anchorPointChange;
	}

	@Override
	public void notifyModelChangeListener() {
		//TODO get rid of this interface if possible, this interface is not required
	}

	/**
	 * Makes a change node iterator comprising of all the shape's properties.
	 * This can be used to create a bigger change node iterator which has additional properties
	 * @return An iterator over all the shape's properties.
	 */
	public final ChangeNodeIterator shapeChangeNodes() {
		ChangeNode[] changeNodes=new ChangeNode[4];
		changeNodes[0]=scaleChange;
		changeNodes[1]=rotationChange;
		changeNodes[2]=translationChange;
		changeNodes[3]=anchorPointChange;
		return new ChangeNodeIterator(changeNodes);
	}
}
