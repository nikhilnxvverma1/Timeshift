package com.nikhil.playback;

/**
 * Created by NikhilVerma on 10/08/15.
 */
public interface TimelinePlayer {
    boolean shouldReceiveTimestep(double delta, double totalTime);
    double getRevisedDeltaTimestep(double delta);
    void didReachEnd();
}
