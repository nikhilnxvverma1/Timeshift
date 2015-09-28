package com.nikhil.command;

import com.nikhil.view.modelview.GraphicalBezierPoint;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class AddBezierPoint implements Command{

    private Pane pane;
    private GraphicalBezierPoint graphicalBezierPoint;
    private ArrayList<GraphicalBezierPoint> graphicalBezierPoints;
    private boolean executed=false;

    public AddBezierPoint(Pane pane, GraphicalBezierPoint graphicalBezierPoint, ArrayList<GraphicalBezierPoint> graphicalBezierPoints, boolean executed) {
        this.pane = pane;
        this.graphicalBezierPoint = graphicalBezierPoint;
        this.graphicalBezierPoints = graphicalBezierPoints;
        this.executed = executed;
    }

    @Override
    public void execute() {
        if(!executed){
            graphicalBezierPoints.add(graphicalBezierPoint);
            graphicalBezierPoint.addAsChildrenTo(pane);
        }

        executed=true;
    }

    @Override
    public void unexecute() {
        graphicalBezierPoint.removeAsChildrenFrom(pane);
        graphicalBezierPoints.remove(graphicalBezierPoint);
        executed=false;
    }
}
