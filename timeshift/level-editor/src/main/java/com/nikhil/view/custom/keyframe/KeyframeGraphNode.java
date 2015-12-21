package com.nikhil.view.custom.keyframe;

import javafx.scene.Group;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public abstract class KeyframeGraphNode extends Group{

    protected KeyframeGraphNodeBezierPoint []graphNodes;

    public void setTime(double time){
        getKeyframeView().setTime(time);
    }

    public double getTime(){
        return getKeyframeView().getTime();
    }

    public void setSelected(boolean selected){
        getKeyframeView().setSelected(selected);
    }

    public boolean isSelected(){
        return getKeyframeView().isSelected();
    }

    public abstract KeyframeGraphNode getNext();
    public abstract KeyframeGraphNode getPrevious();
    public abstract KeyframeView getKeyframeView();

    /**
     * Updates the position of this graph node amongst other graph nodes of the same curve
     * @param graphEditor the graph in which the graph node exists.
     */
    public abstract void update(GraphEditor graphEditor);

}
