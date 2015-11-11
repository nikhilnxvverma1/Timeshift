package com.nikhil.timeline.change.temporal;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * This exception indicates a mismatch in the dimensions of the {@link KeyValue}
 * with respect to other keyframes in the list.
 * Created by NikhilVerma on 11/11/15.
 */
public class MismatchingKeyframeDimensionException extends RuntimeException{

    private int actualDimension;
    private int mismatchedDimension;
    private TemporalKeyframe violatingKeyframe;

    public MismatchingKeyframeDimensionException(int actualDimension, int mismatchedDimension, TemporalKeyframe violatingKeyframe) {
        this.actualDimension = actualDimension;
        this.mismatchedDimension = mismatchedDimension;
        this.violatingKeyframe = violatingKeyframe;
    }

    public int getActualDimension() {
        return actualDimension;
    }

    public int getMismatchedDimension() {
        return mismatchedDimension;
    }

    public TemporalKeyframe getViolatingKeyframe() {
        return violatingKeyframe;
    }
}
