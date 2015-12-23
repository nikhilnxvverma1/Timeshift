package com.nikhil.view.custom.keyframe;

/**
 * Created by NikhilVerma on 20/12/15.
 */
public abstract class GraphNode implements Comparable<GraphNode>{

    protected GraphNodeBezierPoint[] graphNodesBezierPoints;

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

    public abstract KeyframeView getKeyframeView();

    /**
     * Updates the position of this graph node amongst other graph nodes of the same curve
     */
    protected abstract void updateView();

    @Override
    public int compareTo(GraphNode o) {
        return (int) (getTime()-o.getTime());
    }

    public GraphNodeBezierPoint[] getGraphNodesBezierPoints() {
        return graphNodesBezierPoints;
    }

    public void setVisible(boolean visible){
        for (int i = 0; i < graphNodesBezierPoints.length; i++) {
            graphNodesBezierPoints[i].setVisible(visible);
        }
    }
}
