package com.nikhil.timeline.change.fromto;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.interpolation.InterpolationCurve;

/**
 * ChangeNode defines the changes in the values occurring in the timeline.
 * These changes in values from start to end can be handled by a change handler
 * to create vivid rigged time driven simulations.For example : animations can 
 * be performed by making changes in the transformation properties of a view.
 * @author Nikhil Verma
 *
 */
public class FromToChangeNode extends ChangeNode {

	/** defines the delay when two or more nodes are stringed together*/
	public static final float STANDARD_STRING_DELAY=0.1f;

	private double start;
	private double duration;
	protected KeyValue startValue;
	protected KeyValue endValue;
	protected InterpolationCurve curve;
	protected FromToChangeHandler changeHandler;
	
	/**
	 * Creates a new change node for the supplied properties
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param curveStrategy a curve which basically defines the speed of change from start to end
	 * @param startTime when the exactly this node will come in effect on the timeline
	 * @param duration how long would the change take(should not be negative)
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 */
	public FromToChangeNode(FromToChangeHandler changeHandler, InterpolationCurve curveStrategy, double startTime, double duration, KeyValue startValue, KeyValue endValue){
		this.changeHandler=changeHandler;
		this.curve=curveStrategy;
		this.start=startTime;
		this.duration=duration;
		this.startValue=startValue;
		this.endValue=endValue;
	}
	
	/**
	 * Creates a new change node at the end of this node after a certain(possibly negative) delay
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param curveStrategy a curve which basically defines the speed of change from start to end
	 * @param afterDelay delay after which a new node will be created(this can also be negative)
	 * @param duration how long would the change take
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 * @return the newly created node attached right after this node in list
	 */
	public FromToChangeNode addAfterMe(FromToChangeHandler changeHandler,InterpolationCurve curveStrategy,float afterDelay,double duration,KeyValue startValue,KeyValue endValue){
		double startTime=this.start+duration+afterDelay;
		FromToChangeNode nextNodeInList=new FromToChangeNode(changeHandler, curveStrategy, startTime, duration, startValue, endValue);
		insertAfterMe(nextNodeInList);
		return nextNodeInList;
	}

	/**
	 * Creates a new change node at the start of this node after a certain(possibly negative) delay
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param curveStrategy a curve which basically defines the speed of change from start to end
	 * @param afterDelay delay after which a new node will be created(this can also be negative)
	 * @param duration how long would the change take
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 * @return the newly created node attached right after this node in list
	 */
	public FromToChangeNode addWithMe(FromToChangeHandler changeHandler,InterpolationCurve curveStrategy,float afterDelay,double duration,KeyValue startValue,KeyValue endValue){
		double startTime=this.start+afterDelay;
		FromToChangeNode nextNodeInList=new FromToChangeNode(changeHandler, curveStrategy, startTime, duration, startValue, endValue);
		insertAfterMe(nextNodeInList);
		return nextNodeInList;
	}
	
	/**
	 * Creates a new change node at the start of this node with 
	 * 0 delay , the same curve strategy and duration as this node.
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 * @return the newly created node attached right after this node in list
	 */
	public FromToChangeNode addLikeMe(FromToChangeHandler changeHandler,KeyValue startValue,KeyValue endValue){
		return addStringedToMe(changeHandler,0,startValue,endValue);
	}
	
	/**
	 * Creates a new change node at the start of this node after a
	 * fixed delay with the same curve strategy and duration as this node.
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 * @return the newly created node attached right after this node in list
	 */
	public FromToChangeNode addStringedToMe(FromToChangeHandler changeHandler,KeyValue startValue,KeyValue endValue){
		return addStringedToMe(changeHandler,STANDARD_STRING_DELAY,startValue,endValue);
	}
	
	/**
	 * Creates a new change node at the start of this node after a
	 * specified delay with the same curve strategy and duration as this node.
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param stringDelay the delay from start of this node after which the new node will start 
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 * @return the newly created node attached right after this node in list
	 */
	public FromToChangeNode addStringedToMe(FromToChangeHandler changeHandler,float stringDelay,KeyValue startValue,KeyValue endValue){
		return addWithMe(changeHandler,this.curve,stringDelay,this.duration,startValue,endValue);
	}

	public double getStart() {
		return start;
	}

	public void setStart(float start) {
		this.start = start;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public KeyValue getStartValue() {
		return startValue;
	}

	public void setStartValue(KeyValue startValue) {
		this.startValue = startValue;
	}

	public KeyValue getEndValue() {
		return endValue;
	}

	public void setEndValue(KeyValue endValue) {
		this.endValue = endValue;
	}

	public InterpolationCurve getCurve() {
		return curve;
	}

	public void setCurve(InterpolationCurve curve) {
		this.curve = curve;
	}

	@Override
	public boolean step(double delta,double time){

		boolean result;
		if((time >=start)&&(time <=start+duration)){
			result = true;
		}else{
			result = false;
		}
		if(result){
			//during change, compute the interpolated value and trigger value change callback 
			double timeElapsed=time-start;
			double fractionOfDuration=timeElapsed/duration;//this will be between 0.0 to 1.0
			double progression= curve.valueFor(fractionOfDuration);//TODO casting :change all types to double
			KeyValue currentValue= getInterpolatedValue(startValue, endValue, progression);
			changeHandler.valueChanged( time, this, currentValue);

			return true;//indicates change happened
		}else{
			return false;//indicates change did not happen
		}
	}

	@Override
	public void setTime(double time) {
		//during change,
		if((time >=start)&&(time <=start+duration)){
			//compute the interpolated value and trigger value change callback
			double timeElapsed=time-start;
			double fractionOfDuration=timeElapsed/duration;//this will be between 0.0 to 1.0
			double progression= curve.valueFor(fractionOfDuration);
			KeyValue currentValue= getInterpolatedValue(startValue, endValue, progression);
			changeHandler.valueChanged( time, this, currentValue);
		}
		//unnecessary callbacks but what if ther was a jump in time?
		//before change
		else if(time<start){
			changeHandler.valueChanged( time, this, startValue);
		}
		//after change
		else if(time>start+duration){
			changeHandler.valueChanged( time, this, endValue);
		}
	}

	@Override
	public double findEndingTime() {
		return start+duration;
	}

	public void setChangeHandler(FromToChangeHandler changeHandler) {

	}
}
