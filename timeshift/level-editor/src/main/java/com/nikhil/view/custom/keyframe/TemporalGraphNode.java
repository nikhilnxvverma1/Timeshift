package com.nikhil.view.custom.keyframe;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class TemporalGraphNode extends KeyframeGraphNode{

    private TemporalGraphNode previous;
    private TemporalGraphNode next;
    private TemporalKeyframeView keyframeView;

    public TemporalGraphNode(TemporalKeyframeView keyframeView) {
        this.keyframeView = keyframeView;
    }

    @Override
    public TemporalGraphNode getNext() {
        return next;
    }

    @Override
    public TemporalGraphNode getPrevious() {
        return previous;
    }

    @Override
    public TemporalKeyframeView getKeyframeView() {
        return keyframeView;
    }

    @Override
    public void update(GraphEditor graphEditor) {

    }

    public void setNext(TemporalGraphNode next) {
        this.next = next;
    }

    public void setPrevious(TemporalGraphNode previous) {
        this.previous = previous;
    }
}
