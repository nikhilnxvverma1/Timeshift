package com.nikhil.view.custom.keyframe;

import com.nikhil.util.modal.UtilPoint;

/**
 * Spatial/Speed graph nodes deal with speed of change and not the absolute value of the keyframes themselves
 * Created by NikhilVerma on 20/12/15.
 */
public class SpatialGraphNode extends GraphNode {

    private SpatialKeyframeView keyframeView;

    public SpatialGraphNode(SpatialKeyframeView keyframeView) {
        this.keyframeView = keyframeView;
    }

    @Override
    public SpatialKeyframeView getKeyframeView() {
        return keyframeView;
    }

    @Override
    public void updateView() {
        if (true) {
            return;//unsupported
        }
        GraphEditor graphEditor = keyframeView.getKeyframePane().getMetadata().
                getItemViewController().getCompositionViewController().getGraphEditor();
        //time of this graph node/keyframe decides the position on the x axis
        //(which will be same for all)
        double time = getTime();
        double x= ((time - graphEditor.getLowerX()) / (graphEditor.getUpperX() - graphEditor.getLowerX()))
                * graphEditor.getWidth();

        //y position of each node will be different based on the value of each component
        UtilPoint anchorPoint = keyframeView.getKeyframeModel().getBezierPoint().getAnchorPoint();
        for (int i = 0; i < 2; i++) {
            double y;
            if (i == 0){
                y = graphEditor.getHeight() - anchorPoint.getX();
            }else{
                y = graphEditor.getHeight() - anchorPoint.getY();
            }
            graphNodesBezierPoints[i].setAnchorPoint(new UtilPoint(x,y));
        }
    }

    /**
     * Updates the curve(s) of this graph node with corresponding curve(s) of the next graph node
     * @param next the next graph node after this node. This may be null, if there is nothing afterwards
     */
    public void updateView(SpatialGraphNode next) {
        if(true){
            return;//unsupported
        }

        //update position of self and the next
        //while looping over graph nodes,same node will be updated twice
        //once for self,the other time when this is next for the previous node
        //but update is an inexpensive operation. It just sets the positions
        updateView();
        if (next != null) {
            next.updateView();
        }

        //update each graphical bezier point with corresponding next
        for (int i = 0; i < 2; i++) {
            if (next!=null) {
                graphNodesBezierPoints[i].updateView(next.graphNodesBezierPoints[i]);
            }else{
                graphNodesBezierPoints[i].updateView(null);
            }
        }
    }
}
