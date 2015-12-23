package com.nikhil.view.custom.keyframe;

import com.nikhil.timeline.KeyValue;
import com.nikhil.util.modal.UtilPoint;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public class TemporalGraphNode extends GraphNode {

    private TemporalKeyframeView keyframeView;

    public TemporalGraphNode(TemporalKeyframeView keyframeView) {
        this.keyframeView = keyframeView;
        initView(keyframeView);
    }

    private void initView(TemporalKeyframeView keyframeView) {

        //instantiate the bezier points for each key value component
        int dimension = keyframeView.getKeyframeModel().getKeyValue().getDimension();
        graphNodesBezierPoints =new GraphNodeBezierPoint[dimension];
        for (int i = 0; i < dimension; i++) {
            graphNodesBezierPoints[i]=new GraphNodeBezierPoint(new UtilPoint(0,0));
        }
    }

    @Override
    public void updateView() {
        GraphEditor graphEditor = keyframeView.getKeyframePane().getMetadata().
                getItemViewController().getCompositionViewController().getGraphEditor();
        //time of this graph node/keyframe decides the position on the x axis
        //(which will be same for all)
        double time = getTime();
        double x= ((time - graphEditor.getLowerX()) / (graphEditor.getUpperX() - graphEditor.getLowerX()))
                * graphEditor.getWidth();

        //y position of each node will be different based on the value of each component
        KeyValue keyValue = keyframeView.getKeyframeModel().getKeyValue();
        for (int i = 0; i < keyValue.getDimension(); i++) {
            double y=graphEditor.getHeight()-keyValue.get(i);
            graphNodesBezierPoints[i].setAnchorPoint(new UtilPoint(x,y));
        }

    }

    /**
     * Updates the curve(s) of this graph node with corresponding curve(s) of the next graph node
     * @param next the next graph node after this node. This may be null, if there is nothing afterwards but if not,
     *             then the associated key value must have the same dimensions.
     */
    public void updateView(TemporalGraphNode next){
        //the dimension has to be the same
        final int dimension = keyframeView.getKeyframeModel().getKeyValue().getDimension();
        if(next!=null&&
                next.keyframeView.getKeyframeModel().getKeyValue().getDimension()!=dimension){
            throw new RuntimeException("Dimension mismatch of the key value in the graph nodes");
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
        for (int i = 0; i < dimension; i++) {
            if (next!=null) {
                graphNodesBezierPoints[i].updateView(next.graphNodesBezierPoints[i]);
            }else{
                graphNodesBezierPoints[i].updateView(null);
            }
        }
    }

    @Override
    public TemporalKeyframeView getKeyframeView() {
        return keyframeView;
    }
}
