package com.nikhil.playback;

/**
 * Created by NikhilVerma on 10/08/15.
 */
public interface TimelinePlayer {
    public boolean shouldRecieveTimestep(double delta, double totalTime);
    public double getRevisedDeltaTimestep(double delta);
    public void didReachEnd();
}
