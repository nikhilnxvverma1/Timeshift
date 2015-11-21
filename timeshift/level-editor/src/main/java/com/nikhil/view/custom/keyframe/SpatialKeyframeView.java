package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.SpatialKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframeView extends KeyframeView{

    private SpatialKeyframe keyframe;

    public SpatialKeyframeView(SpatialKeyframe keyframe) {
        this(keyframe,DEFAULT_WIDTH);
    }

    public SpatialKeyframeView(SpatialKeyframe keyframe,double width) {
        super(width);
        this.keyframe = keyframe;
    }

    @Override
    public double getTime() {
        return keyframe.getTime();
    }

    @Override
    public SpatialKeyframe getKeyframeModel() {
        return keyframe;
    }
}
