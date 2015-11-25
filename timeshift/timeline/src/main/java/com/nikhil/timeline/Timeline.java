package com.nikhil.timeline;

import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.change.ChangeNodeIterator;

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
	private double time;
	private boolean timeMovingForward=true;
	private TimelineReachedTerminal timelineReachedTerminal;
	
	/**
	 * Creates a new timeline using the tail of the list of change nodes
	 * @param tail tail of the list.this can be used in making concatenated ChangeNode in code
	 */
	public Timeline(ChangeNode tail){
		this(null,tail);
	}
	
	/**
	 * Creates a new timeline with a completion delegate using the tail of the list of change nodes 
	 * @param timelineReachedTerminal completion delegate thats triggered when timeline reaches end or beginning
	 * @param tail tail of the list.this can be used in making concatenated ChangeNode in code 
	 */
	public Timeline(TimelineReachedTerminal timelineReachedTerminal,ChangeNode tail){
		this.tail=tail;
		this.head=tail!=null?tail.getFirstNode():null;
		this.timelineReachedTerminal = timelineReachedTerminal;
	}

	public double getTime() {
		return time;
	}

	/**
	 * sets the current time on the timeline. 
	 * This will instantly affect all the associated change nodes and
	 * fire off their respective change handlers.
	 * @param time the time to seek to on timeline
	 */
	public void setTime(float time) {

		this.time = time;
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
	
	public TimelineReachedTerminal getTimelineReachedTerminal() {
		return timelineReachedTerminal;
	}

	public void setTimelineReachedTerminal(TimelineReachedTerminal timelineReachedTerminal) {
		this.timelineReachedTerminal = timelineReachedTerminal;
	}
	
	/**
	 * Simply appends the argument change node(possibly list) to the its list
	 * @param changeNode a single change node or a linked list of change nodes
	 */
	public void add(ChangeNode changeNode){
		if(head==null&&tail==null){
			head=changeNode;
			tail=changeNode.getLastNode();
		}else{
			tail=tail.insertAfterMe(changeNode);
		}
	}

	/**
	 * Appends all the change nodes from the supplied iterator.
	 * This method also does not check for containment of any nodes in the list.
	 * For each change node, the next and previous will be nullified.
	 * @param iterator iterator that allows iterating through all the change nodes
	 */
	public void add(ChangeNodeIterator iterator){
		if(iterator==null){
			return;
		}

		//iterate through and add all the change nodes at the end
		while(iterator.hasNext()){
			ChangeNode changeNode = iterator.next();
			changeNode.setPrevious(null);
			changeNode.setNext(null);
			add(changeNode);
		}
	}

	/**
	 * steps the timeline forward
	 * @param delta timestep to step timeline forward
	 * @return true if any change is still taking place or pending to take place,false otherwise
	 */
	public boolean step(double delta){//TODO make the argument in double
		//step the current time forward or backward depending on flag value
		if(timeMovingForward){
			time +=delta;
		}else{
			time -=delta;
		}

		//flag that keeps track of any node still pending completion
		boolean pendingCompletion=false;
		//traverse the list
		ChangeNode t=head;
		while(t!=null){
			//update all nodes
			boolean nodePendingCompletion=t.step(delta, time);
			if(nodePendingCompletion){
				pendingCompletion=true;
			}
			t=t.getNext();
		}
		
		//if a delegate exists fire the callback
		//TODO doesnt work for time moving backward
		if((!pendingCompletion)&&(timelineReachedTerminal !=null)){
			timelineReachedTerminal.timelineReachedTerminal(this);
		}
		return pendingCompletion;
	}
	
	/**
	 * Removes the change node from the list of change nodes linked list.
	 * This does not check if the node already exists in the list
	 * @param changeNode Single change node to remove.
	 */
	public void remove(ChangeNode changeNode){
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
	 * Removes the change node from the list of change nodes linked list
	 * using the supplied iterator. This does not check if any nodes were already
	 * present in the list
	 * @param iterator iterator that allows iterating through change nodes to remove
	 */
	public void remove(ChangeNodeIterator iterator){
		while(iterator.hasNext()){
			remove(iterator.next());
		}
	}

	/**
	 * Computes the ending time for this timeline
	 * @return the ending time at which the timeline finishes
	 */
	public double findEndingTime(){
		double endingTime=0;
		ChangeNode t=head;
		while(t!=null){
			double currentChangeNodeEndingTime=t.findEndingTime();
			if(currentChangeNodeEndingTime>endingTime){
				endingTime=currentChangeNodeEndingTime;
			}
			t=t.getNext();
		}
		return endingTime;
	}
}
