package com.nikhil.timeline.changehandler;

import com.nikhil.timeline.ChangeNode;
import com.nikhil.timeline.Timeline;

/**
 * An abstract class that implements ChangeHandler. 
 * This class implements valueAtStart and valueAtEnd callbacks
 * but not valueChanged callback.
 * Use this to make quick anonymous Change Handlers.
 * @author Nikhil Verma
 *
 */
public abstract class SimpleChange implements ChangeHandler{

	@Override
	public void valueAtStart(Timeline timeline, ChangeNode curveNode) {
		//optional method
		//deliberately left empty
	}

	@Override
	public void valueAtEnd(Timeline timeline, ChangeNode curveNode) {
		//optional method
		//deliberately left empty
	}

}
