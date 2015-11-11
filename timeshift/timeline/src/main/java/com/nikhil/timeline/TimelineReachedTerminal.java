package com.nikhil.timeline;

/**
 * Delegate for what to do after timeline reaches one of the endpoints(moving forward or backward in time)
 * @author Nikhil Verma
 *
 */
public interface TimelineReachedTerminal {

	/**
	 * callback whenever timeline reaches its end weather moving forward or backward 
	 * @param timeline timeline which can move both forward or backward in time
	 */
	public void timelineReachedTerminal(Timeline timeline);
	
}
