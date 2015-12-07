package com.nikhil.timeline.change;

import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * Base class for each keyframe change node.
 * Created by NikhilVerma on 25/11/15.
 */
public abstract class KeyframeChangeNode extends ChangeNode{

    @Override
    public double findEndingTime() {
        return (getLast() == null) ? 0 : getLast().getTime();
    }

    /**
     * @return true if this keyframe change node doesn't have any keyframes
     */
    public boolean isEmpty(){
        return (getStart() == null) && (getLast() == null);
    }

    public abstract Keyframe getStart();
    public abstract Keyframe getLast();
    /**
     * Finds a nearby keyframe (if any) for the given time and margin.
     * Practically, this is an O(1) operation if you are seeking through the timeline
     * because the nearest keyframe is cached, otherwise its O(n)
     * @param time time near which a keyframe needs to be found
     * @param nearByMargin defines a range around a time, between which keyframes are considered acceptable.
     *                     This must always be >=0.
     * @return a nearby keyframe if it exists within margin,else null (null, also in case of empty list)
     */
    public abstract Keyframe findNearbyKeyframe(double time,double nearByMargin);
}
