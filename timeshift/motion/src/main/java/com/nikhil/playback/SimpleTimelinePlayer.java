package com.nikhil.playback;

/**
 * Simple player that receives step as soon as stepped
 * and doesnt revise the step given.(i.e returns the same delta step).
 * Created by NikhilVerma on 10/08/15.
 */
public class SimpleTimelinePlayer implements TimelinePlayer{

    @Override
    public boolean shouldReceiveTimestep(double delta, double totalTime) {
        return true;
    }

    @Override
    public double getRevisedDeltaTimestep(double delta) {
        return delta;
    }

    @Override
    public void didReachEnd() {
        //unhandled
    }
}
