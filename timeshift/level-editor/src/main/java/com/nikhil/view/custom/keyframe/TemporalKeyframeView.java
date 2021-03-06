package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.keyframe.TemporalKeyframe;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalKeyframeView extends KeyframeView{

    private TemporalKeyframe keyframeModel;
    private TemporalKeyframePane keyframePane;
    private TemporalGraphNode graphNode;

    public TemporalKeyframeView(TemporalKeyframe keyframeModel,TemporalKeyframePane keyframePane) {
        this(keyframeModel,keyframePane,DEFAULT_WIDTH);
    }

    public TemporalKeyframeView(TemporalKeyframe keyframeModel,TemporalKeyframePane keyframePane, double width) {
        super(width);
        this.keyframeModel = keyframeModel;
        this.keyframePane=keyframePane;
        this.graphNode=new TemporalGraphNode(this);
    }

    @Override
    public TemporalGraphNode getGraphNode() {
        return graphNode;
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

        //Sorting trick: remove and add back to insert the graph node in sorted order
        keyframePane.getGraphNodes().remove(graphNode);
        keyframePane.getGraphNodes().add(graphNode);

        //update the (possibly changed) order of graph nodes
        TemporalGraphNode before= keyframePane.getGraphNodes().floor(graphNode);
        if (before!=null) {
            before.updateView(graphNode);
        }
        TemporalGraphNode after = keyframePane.getGraphNodes().ceiling(graphNode);
        if (after!=null) {
            graphNode.updateView(after);
        }
    }
}
