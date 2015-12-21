package com.nikhil.view.custom.keyframe;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class SpatialGraphNode extends KeyframeGraphNode {

    private SpatialGraphNode previous;
    private SpatialGraphNode next;
    private SpatialKeyframeView keyframeView;

    public SpatialGraphNode(SpatialKeyframeView keyframeView) {
        this.keyframeView = keyframeView;
    }

    public void setPrevious(SpatialGraphNode previous) {
        this.previous = previous;
    }

    public void setNext(SpatialGraphNode next) {
        this.next = next;
    }

    @Override
    public SpatialGraphNode getNext() {
        return next;
    }

    @Override
    public SpatialGraphNode getPrevious() {
        return previous;
    }

    @Override
    public SpatialKeyframeView getKeyframeView() {
        return keyframeView;
    }

    @Override
    public void update(GraphEditor graphEditor) {

    }
}
