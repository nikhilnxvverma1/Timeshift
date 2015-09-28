package com.nikhil.common;

/**
 * Every View container must implement this interface ,
 * so it knows when the business model changed,
 * and therefore make corresponding changes to its view
 * @author Nikhil Verma
 *
 */
public interface Observer {

	/**Callback for any change made in the business models.*/
	public void update(Subject subject);
}
