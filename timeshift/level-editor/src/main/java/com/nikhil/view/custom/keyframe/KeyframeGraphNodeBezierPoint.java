package com.nikhil.view.custom.keyframe;

import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.bezier.GraphicalBezierPoint;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class KeyframeGraphNodeBezierPoint extends GraphicalBezierPoint{

    private KeyframeGraphNode keyframeGraphNode;

    protected KeyframeGraphNodeBezierPoint(UtilPoint anchorPoint) {
        super(anchorPoint);
    }
}
