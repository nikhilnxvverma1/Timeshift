package com.nikhil.timeline.change.fromto;

import com.nikhil.timeline.change.ChangeNode;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.Timeline;

/**
 * Delegate for handling changes in the current value of a change node.  
 * @author Nikhil Verma
 *
 */
public interface FromToChangeHandler {

	/**
	 * Fired whenever current value of the node changes with time.
	 * In case of a jump in timeline's time , this callback WILL be fired
	 * @param currentTime the current time of the timeline
	 * @param changeNode parent curve node responsible for this change.
	 * @param changedValue the current value of the node
	 */
	void valueChanged(double currentTime, ChangeNode changeNode, KeyValue changedValue);
}
