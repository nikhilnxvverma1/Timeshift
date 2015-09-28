package com.nikhil.timeline;

/**
 * Timeline class holds all the nodes for value changes in a double linked list.
 * Using this list , animation can be performed by time stepping through 
 * the animation .Speed of change in values is controlled using a Curves strategy such as 
 * Linear,Bezier(for easing) and several others. 
 * All time(s) and duration are in seconds
 * @author Nikhil Verma
 *
 */
public class Timeline {

	/**general purpose tag that the client can safely use(not used by timeline),default is -1*/
	public int tag=-1;
	
	private ChangeNode head;
	private ChangeNode tail;
	private float currentTime;
	private boolean timeMovingForward=true;
	private TimelineTerminal timelineTerminal;
	
	/**
	 * Creates a new timeline using the tail of the list of change nodes
	 * @param tail tail of the list.this can be used in making concatenated ChangeNode in code 
	 */
	public Timeline(ChangeNode tail){
		this(null,tail);
	}
	
	/**
	 * Creates a new timeline with a completion delegate using the tail of the list of change nodes 
	 * @param timelineTerminal completion delegate thats triggered when timeline reaches end or beginning
	 * @param tail tail of the list.this can be used in making concatenated ChangeNode in code 
	 */
	public Timeline(TimelineTerminal timelineTerminal,ChangeNode tail){
		this.tail=tail;
		this.head=tail!=null?tail.getFirstNode():null;
		this.timelineTerminal = timelineTerminal;
	}
	
	public ChangeNode getHead() {
		return head;
	}
	public void setHead(ChangeNode head) {
		this.head = head;
	}
	public ChangeNode getTail() {
		return tail;
	}
	public void setTail(ChangeNode tail) {
		this.tail = tail;
	}
	
	public float getCurrentTime() {
		return currentTime;
	}

	/**
	 * sets the current time on the timeline. 
	 * This will instantly jump the current time to the specified value thereby
	 * affecting the changes in the keyvalues.
	 * Note the side effect : change handlers wont fire "atBegin" and "atEnd" callbacks 
	 * @param currentTime the time to seek to on timeline
	 */
	public void setCurrentTime(float currentTime) {
		this.currentTime = currentTime;
	}

	public boolean isTimeMovingForward() {
		return timeMovingForward;
	}

	/**
	 * sets the direction of time , by default this is true
	 * @param timeMovingForward weather time should progress forward or backward
	 */
	public void setTimeMovingForward(boolean timeMovingForward) {
		this.timeMovingForward = timeMovingForward;
	}
	
	public TimelineTerminal getTimelineTerminal() {
		return timelineTerminal;
	}

	public void setTimelineTerminal(TimelineTerminal timelineTerminal) {
		this.timelineTerminal = timelineTerminal;
	}
	
	/**
	 * Simply adds the argument change node(possibly list) to the its list 
	 * @param changeNode a single change node or a list of change nodes
	 */
	public void appendChangeNodeToList(ChangeNode changeNode){
		if(head==null&&tail==null){
			head=changeNode;
			tail=changeNode.getLastNode();
		}else{
			tail=tail.insertAfterMe(changeNode);
		}
	}

	/**
	 * steps the timeline forward
	 * @param delta timestep to step timeline forward
	 * @return true if any change is still taking place or pending to take place,false otherwise
	 */
	public boolean step(float delta){//TODO make the argument in double
		//step the current time forward or backward depending on flag value
		if(timeMovingForward){
			currentTime+=delta;
		}else{
			currentTime-=delta;
		}

		//flag that keeps track of any node still pending completion
		boolean pendingCompletion=false;
		//traverse the list
		ChangeNode t=head;
		while(t!=null){
			//update all nodes
			boolean nodePendingCompletion=t.step(delta, this);
			if(nodePendingCompletion){
				pendingCompletion=true;
			}
			t=t.getNext();
		}
		
		//if a delegate exists fire the callback
		//TODO doesnt work for time moving backward
		if((!pendingCompletion)&&(timelineTerminal !=null)){
			timelineTerminal.timelineReachedTerminal(this);
		}
		return pendingCompletion;
	}
	
	/**
	 * Removes the change node from the list of change nodes linked list.
	 * This does not check if the node already exists in the list
	 * @param changeNode change node to remove
	 */
	public void removeChangeNodeFromList(ChangeNode changeNode){
		if(changeNode==head){
			if(changeNode==tail){
				head=null;
				tail=null;
			}else if(changeNode.getNext()==tail){
				head=changeNode.getNext();
				changeNode.removeFromList();
			}else{
				changeNode.removeFromList();
			}
		}else if(changeNode==tail){
			if(changeNode.getPrevious()==head){
				tail=changeNode.getPrevious();
				changeNode.removeFromList();
			}else{
				changeNode.removeFromList();
			}
		}else{
			changeNode.removeFromList();
		}
	}
	
	/**
	 * Computes the ending time for this timeline
	 * @return the ending time at which the timeline finishes
	 */
	public float getEndingTime(){
		float endingTime=0;
		ChangeNode t=head;
		while(t!=null){
			float currentChangeNodeEndingTime=t.getStart()+t.getDuration();
			if(currentChangeNodeEndingTime>endingTime){
				endingTime=currentChangeNodeEndingTime;
			}
			t=t.getNext();
		}
		return endingTime;
	}
}
