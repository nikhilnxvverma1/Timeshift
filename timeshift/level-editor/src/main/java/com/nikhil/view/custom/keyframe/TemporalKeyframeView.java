package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframeView extends KeyframeView{

    private TemporalKeyframe keyframeModel;
    private TemporalKeyframePane keyframePane;

    public TemporalKeyframeView(TemporalKeyframe keyframeModel,TemporalKeyframePane keyframePane) {
        this(keyframeModel,keyframePane,DEFAULT_WIDTH);
    }

    public TemporalKeyframeView(TemporalKeyframe keyframeModel,TemporalKeyframePane keyframePane, double width) {
        super(width);
        this.keyframeModel = keyframeModel;
        this.keyframePane=keyframePane;
    }

    @Override
    public TemporalKeyframePane getKeyframePane() {
        return keyframePane;
    }

    @Override
    public TemporalKeyframe getKeyframeModel() {
        return keyframeModel;
    }

    @Override
    public void addToParentKeyframePane() {
        keyframePane.addKeyframe(this);
    }

    @Override
    public boolean removeFromParentKeyframePane() {
        return keyframePane.removeKeyframe(this);
    }

    @Override
    protected void shiftTimeInChangeNode(double time) {
        keyframePane.getMetadata().getKeyframeChangeNode().shiftKeyframe(keyframeModel,time);
    }
}
