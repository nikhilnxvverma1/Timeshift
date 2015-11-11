package com.nikhil.space;

import com.nikhil.util.modal.UtilPoint;

/**
 * Parametric curve for 2D coordinates system
 * @author Nikhil Verma
 *
 */
public interface Parametric {
	/**
	 * gets a 2D point for a value between 0.0 to 1.0
	 * @param t a value between 0.0 to 1.0
	 * @return 2D point which lies on the Parametric curve
	 */
	UtilPoint getPointFor(double t);
}
