package com.nikhil.timeline;

import com.nikhil.timeline.changehandler.ChangeHandler;
import com.nikhil.timeline.interpolation.InterpolationCurve;

/**
 * ChangeNode defines the changes in the values occuring in the timeline.
 * These changes in values from start to end can be handled by a change handler
 * to create vivid rigged time driven simulations.For example : animations can 
 * be performed by making changes in the transformation properties of a view.
 * @author Nikhil Verma
 *
 */
public class ChangeNode {

	/** defines the delay when two or more nodes are stringed together*/
	public static final float STANDARD_STRING_DELAY=0.1f;

	/**general purpose tag that the client can safely use(not used by change node),default is -1*/
	public int tag=-1;
	
	private float start;
	private float duration;
	protected KeyValue startValue;
	protected KeyValue endValue;
	protected InterpolationCurve curve;
	private ChangeHandler changeHandler;
	
	private ChangeNode next;
	private ChangeNode previous;
	
	
	
	/**
	 * Creates a new change node for the supplied properties
	 * @param changeHandler a delegate that manages the changes in values from start value to end value as time progresses
	 * @param curveStrategy a curve which basically defines the speed of change from start to end
	 * @param startTime when the exactly this node will come in effect on the timeline
	 * @param duration how long would the change take(should not be negative)
	 * @param startValue starting value before the change
	 * @param endValue end value at the end of the change
	 */
	public ChangeNode(ChangeHandler changeHandler,InterpolationCurve curveStrategy,float startTime,float duration,KeyValue startValue,KeyValue endValue){
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
	public ChangeNode addAfterMe(ChangeHandler changeHandler,InterpolationCurve curveStrategy,float afterDelay,float duration,KeyValue startValue,KeyValue endValue){
		float startTime=this.start+duration+afterDelay;
		ChangeNode nextNodeInList=new ChangeNode(changeHandler, curveStrategy, startTime, duration, startValue, endValue);
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
	public ChangeNode addWithMe(ChangeHandler changeHandler,InterpolationCurve curveStrategy,float afterDelay,float duration,KeyValue startValue,KeyValue endValue){
		float startTime=this.start+afterDelay;
		ChangeNode nextNodeInList=new ChangeNode(changeHandler, curveStrategy, startTime, duration, startValue, endValue);
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
	public ChangeNode addLikeMe(ChangeHandler changeHandler,KeyValue startValue,KeyValue endValue){
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
	public ChangeNode addStringedToMe(ChangeHandler changeHandler,KeyValue startValue,KeyValue endValue){
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
	public ChangeNode addStringedToMe(ChangeHandler changeHandler,float stringDelay,KeyValue startValue,KeyValue endValue){
		return addWithMe(changeHandler,this.curve,stringDelay,this.duration,startValue,endValue);
	}
	
	/**
	 * inserts a node(or list of nodes) after this node in list taking care of the nodes 
	 * currently ahead of this node 
	 * @param nextNodeInList node(or list of nodes) to insert after this node in list.
	 * if null, this call is ignored and returns "this" object
	 * @return the last node of the argument list just inserted ,or "this" if argument is null
	 */
	public ChangeNode insertAfterMe(ChangeNode nextNodeInList) {
		if(nextNodeInList==null){
			return this;
		}
		ChangeNode lastNode=nextNodeInList.getLastNode();
		lastNode.next=this.next;
//		nextNodeInList.next=this.next;
		if(this.next!=null){
//			this.next.previous=nextNodeInList;
			this.next.previous=lastNode;
		}
		nextNodeInList.previous=this;
		this.next=nextNodeInList;
//		return nextNodeInList;
		return lastNode;
	}

	public float getStart() {
		return start;
	}

	public void setStart(float start) {
		this.start = start;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
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

	public ChangeHandler getChangeHandler() {
		return changeHandler;
	}

	public void setChangeHandler(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	public ChangeNode getNext() {
		return next;
	}

	public void setNext(ChangeNode next) {
		this.next = next;
	}

	public ChangeNode getPrevious() {
		return previous;
	}

	public void setPrevious(ChangeNode previous) {
		this.previous = previous;
	}
	
	/**
	 * traverses the entire list and gives the first node in list
	 * @return head of the linked list
	 */
	public ChangeNode getFirstNode(){
		ChangeNode t=this;
		ChangeNode firstNode;
		//traverse till beginning
		do{
			//preserve the node now
			firstNode=t;
			//go till the very beginning
			t=t.previous;
		}while(t!=null);
		
		return firstNode;
	}
	
	/**
	 * traverses the entire list and gives the last node in list
	 * @return tail of the linked list
	 */
	public ChangeNode getLastNode(){
		ChangeNode t=this;
		ChangeNode lastNode;
		//traverse till end
		do{
			//preserve the node before
			lastNode=t;
			//go till the very end
			t=t.next;
		}while(t!=null);
		
		return lastNode;
	}
	
	public boolean isActive(float time){
		if((time>=start)&&(time<=start+duration)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * steps the change node ahead by delta time
	 * 
	 * @param delta timestep that was used to increment timeline forward
	 * @param timeline timeline on which this change is occurring
	 * @return true if this change node is still pending or 
	 * in the process of completing itself,false otherwise
	 */
	public boolean step(float delta,Timeline timeline){
		boolean isPendingCompletion=true;
		final float endTime = start+duration;
		
		//before change
		if(timeline.getCurrentTime()<start){
			
			//completion depends on the direction of time
			if(timeline.isTimeMovingForward()){
				isPendingCompletion=true;
			}else{
				isPendingCompletion=false;
			}

			//trigger begin callback if delta decrement just crossed start time
			if((!timeline.isTimeMovingForward())&&(timeline.getCurrentTime()+delta>=start)){
				changeHandler.valueChanged(timeline, this, startValue);//value changed once after crossing boundary
				changeHandler.valueAtStart(timeline, this);
			}
		}
		//during change
		else if(isActive(timeline.getCurrentTime())){
			isPendingCompletion=true;

			//trigger begin callback if delta increment just crossed start time
			if((timeline.isTimeMovingForward())&&(timeline.getCurrentTime()-delta<start)){
				changeHandler.valueChanged(timeline, this, startValue);//value changed once after crossing boundary
				changeHandler.valueAtStart(timeline, this);
			}

			//during change, compute the interpolated value and trigger value change callback 
			float timeElapsed=timeline.getCurrentTime()-start;
			float fractionOfDuration=timeElapsed/duration;//this will be between 0.0 to 1.0
			float progression= (float) curve.valueFor(fractionOfDuration);//TODO casting :change all types to double
			KeyValue currentValue=getInterpolatedValue(progression);
			changeHandler.valueChanged(timeline, this, currentValue);

			//trigger end callback if delta decrement just crossed end time
			if((!timeline.isTimeMovingForward())&&(timeline.getCurrentTime()+delta>endTime)){
				changeHandler.valueChanged(timeline, this, endValue);//value changed once after crossing boundary
				changeHandler.valueAtEnd(timeline, this);
			}
		}
		//after change
		else{
			//completion depends on the direction of time
			if(timeline.isTimeMovingForward()){
				isPendingCompletion=false;
			}else{
				isPendingCompletion=true;
			}

			//trigger end callback if delta increment just crossed end time
			if((timeline.isTimeMovingForward())&&(timeline.getCurrentTime()-delta<=endTime)){
				changeHandler.valueChanged(timeline, this, endValue);//value changed once after crossing boundary
				changeHandler.valueAtEnd(timeline, this);
			}
		}

		return isPendingCompletion;
	}
	
	/**
	 * gets the interpolated value between the starting and the ending values
	 * @param progression between 0.0 to 1.0
	 * @return interpolated value of the same dimension
	 */
	public KeyValue getInterpolatedValue(float progression){
		KeyValue interpolatedValue=new KeyValue(startValue);
		for(int i=0;i<interpolatedValue.getDimension();i++){
			double progressedValue=startValue.getValue(i)+progression*(endValue.getValue(i)-startValue.getValue(i));
			interpolatedValue.setValue(i, progressedValue);
		}
		return interpolatedValue;
	}
	
	/**
	 * Removes this node from the list 
	 * by simply taking care of its next and previous references.
	 * This does not check the head and tail references in a timeline.
	 * Use {@code Timeline.removeChangeNodeFromList(ChangeNode)} for those checks
	 */
	public void removeFromList(){
		if(previous!=null){
			previous.next=next;
		}
		if(next!=null){
			next.previous=previous;
		}
		
		//nullify existing
		previous=null;
		next=null;
	}
}
