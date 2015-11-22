package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.SpatialKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframeView extends KeyframeView{

    private SpatialKeyframe keyframe;
    private SpatialKeyframePane keyframePane;

    public SpatialKeyframeView(SpatialKeyframe keyframe,SpatialKeyframePane keyframePane) {
        this(keyframe,keyframePane,DEFAULT_WIDTH);
    }

    public SpatialKeyframeView(SpatialKeyframe keyframe,SpatialKeyframePane keyframePane,double width) {
        super(width);
        this.keyframe = keyframe;
        this.keyframePane=keyframePane;
    }

    @Override
    public KeyframePane getKeyframePane() {
        return keyframePane;
    }

    @Override
    public SpatialKeyframe getKeyframeModel() {
        return keyframe;
    }

    @Override
    public void addToParentKeyframePane() {
        //TODO
    }

    @Override
    public boolean removeFromParentKeyframePane() {
        //TODO
        return false;
    }
}
