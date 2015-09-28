package com.nikhil.common;

/**
 * Pulse in the system infers to a step in time either forward or backward.
 * Implement this interface if you want to be notified of this step.
 * Additionally register the implementing class in the root controller
 * Created by NikhilVerma on 11/08/15.
 */
public interface PulseListener {

    public void step(double delta, double totalTimeInContext);
}
