package com.nikhil.timeline;

/**
 * delegate for what to do after timeline reaches one of the endpoints(moving forward or backward in time)
 * @author Home
 *
 */
public interface TimelineTerminal {

	/**
	 * callback whenever timeline reaches its end weather moving forward or backward 
	 * @param timeline timeline which can move both forward or backward in time
	 */
	public void timelineReachedTerminal(Timeline timeline);
	
}
