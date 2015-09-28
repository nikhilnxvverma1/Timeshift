package com.nikhil.timeline;

import com.nikhil.space.Parametric;
import com.nikhil.timeline.changehandler.ChangeHandler;
import com.nikhil.timeline.interpolation.InterpolationCurve;
import com.nikhil.util.modal.UtilPoint;
/**
 * Spatial Change node is specifically useful for positional changes ,
 * which can be defined using a spatialValues parametric curve.
 * Additionally it can also have temporal component to it.
 */
public class SpatialChangeNode extends ChangeNode{
	
	private Parametric spatialValues;

	/**
	 * Creates a new Spatial change node for the supplied properties.
	 * 
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses.
	 * Keep in mind, first two values are always going to be position data(x,y) and 
	 * rest of the values are going to be from temporal component
	 * @param curveStrategy a curve which basically defines the speed of change from start to end
	 * @param startTime when the exactly this node will come in effect on the timeline
	 * @param duration how long would the change take(should not be negative)
	 * @param startValue starting temporal value before the change.Set null if not required
	 * @param endValue ending temporal value at the end of the change.Set null if not required
	 * @param spatialValues interpolated positional values in space as defined by a parametric curve 
	 */
	public SpatialChangeNode(ChangeHandler changeHandler,
			InterpolationCurve curveStrategy, float startTime, float duration,
			KeyValue startValue, KeyValue endValue,Parametric spatialValues) {
		
		//since temporal component could be null, make a check and if needed fill it with augmented value
		super(changeHandler,
				curveStrategy,
				startTime,
				duration, 
				startValue==null?new KeyValue(0):startValue,
				endValue==null?new KeyValue(1):endValue);
		this.spatialValues=spatialValues;
	}
	
	/**
	 * gets the interpolated values in space between the starting 
	 * and the ending values,
	 * @param progression between 0.0 to 1.0
	 * @return interpolated value where first two values are position x,y
	 *  and rest are values from the temporal component 
	 */
	public KeyValue getInterpolatedValue(float progression){
		KeyValue interpolatedValue=new KeyValue(0,0,startValue);
		UtilPoint position=spatialValues.getPointFor(progression);
		interpolatedValue.setValue(0, (float) position.getX());
		interpolatedValue.setValue(1, (float) position.getY());
		for(int i=0;i<startValue.getDimension();i++){
			float progressedValue=startValue.getValue(i)+progression*(endValue.getValue(i)-startValue.getValue(i));
			interpolatedValue.setValue(i+2, progressedValue);
		}
		return interpolatedValue;
	}

	public Parametric getSpatialValues() {
		return spatialValues;
	}

	public void setSpatialValues(Parametric spatialValues) {
		this.spatialValues = spatialValues;
	}
	
}
