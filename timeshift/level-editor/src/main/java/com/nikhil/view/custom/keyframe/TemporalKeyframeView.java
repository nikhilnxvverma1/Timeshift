package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframeView extends KeyframeView{

    private TemporalKeyframe keyframe;

    public TemporalKeyframeView(TemporalKeyframe keyframe) {
        this(keyframe,DEFAULT_WIDTH);
    }

    public TemporalKeyframeView(TemporalKeyframe keyframe, double width) {
        super(width);
        this.keyframe=keyframe;
    }

    @Override
    public double getTime() {
        return keyframe.getTime();
    }
}
