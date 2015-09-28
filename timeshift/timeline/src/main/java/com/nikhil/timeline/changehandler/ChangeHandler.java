package com.nikhil.timeline.changehandler;

import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Delegate for handling changes in the current value of a change node.  
 * @author Nikhil Verma
 *
 */
public interface ChangeHandler {

	/**
	 * Fired when time reaches the start of the change node.
	 * Note that this callback WILL NOT be fired if there is a jump in timeline's time. 
	 * @param timeline main timeline under which this event was fired.
	 * @param changeNode parent curve node responsible for this change.
	 */
	public void valueAtStart(Timeline timeline,ChangeNode changeNode);
	/**
	 * Fired whenever current value of the node changes with time.
	 * Incase of a jump in timeline's time , this callback WILL be fired
	 * @param timeline main timeline under which this event was fired.
	 * @param changeNode parent curve node responsible for this change.
	 * @param changedValue the current value of the node
	 */
	public void valueChanged(Timeline timeline,ChangeNode changeNode,KeyValue changedValue);
	/**
	 * Fired when time reaches the end of the change node.
	 * Note that this callback WILL NOT be fired if there is a jump in timeline's time.
	 * @param timeline main timeline under which this event was fired.
	 * @param changeNode parent curve node responsible for this change.
	 */
	public void valueAtEnd(Timeline timeline,ChangeNode changeNode);
}
