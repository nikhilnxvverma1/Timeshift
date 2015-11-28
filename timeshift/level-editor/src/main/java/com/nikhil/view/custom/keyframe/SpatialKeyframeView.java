package com.nikhil.view.custom.keyframe;

import com.nikhil.command.AddTemporalKeyframe;
import com.nikhil.command.ModifyTemporalKeyframe;
import com.nikhil.command.RotateShape;
import com.nikhil.command.TemporalActionOnSingleItem;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.bezier.InteractiveBezierPoint;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialKeyframeView extends KeyframeView{
    private SpatialKeyframe keyframe;
    private SpatialKeyframePane keyframePane;
    private InteractiveBezierPoint interactiveBezierPoint;

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
