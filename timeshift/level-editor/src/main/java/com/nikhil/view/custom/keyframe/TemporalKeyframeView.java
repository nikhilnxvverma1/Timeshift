package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframeView extends KeyframeView{

    private TemporalKeyframe keyframeModel;

    public TemporalKeyframeView(TemporalKeyframe keyframeModel) {
        this(keyframeModel,DEFAULT_WIDTH);
    }

    public TemporalKeyframeView(TemporalKeyframe keyframeModel, double width) {
        super(width);
        this.keyframeModel = keyframeModel;
    }

    @Override
    public double getTime() {
        return keyframeModel.getTime();
    }

    @Override
    public TemporalKeyframe getKeyframeModel() {
        return keyframeModel;
    }
}
