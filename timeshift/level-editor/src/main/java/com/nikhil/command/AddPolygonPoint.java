package com.nikhil.command;

import com.nikhil.view.bezier.GraphicalPolygonPoint;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class AddPolygonPoint extends Command {

    private Pane pane;
    private GraphicalPolygonPoint graphicalPolygonPoint;
    private ArrayList<GraphicalPolygonPoint> graphicalPolygonPointArrayList;
    private boolean executed=false;

    public AddPolygonPoint(Pane pane, GraphicalPolygonPoint graphicalPolygonPoint, ArrayList<GraphicalPolygonPoint> graphicalPolygonPointArrayList, boolean executed) {
        this.pane = pane;
        this.graphicalPolygonPointArrayList = graphicalPolygonPointArrayList;
        this.graphicalPolygonPoint = graphicalPolygonPoint;
        this.executed = executed;
    }

    @Override
    public void execute() {
        if(!executed){
            graphicalPolygonPoint.addAsChildrenTo(pane);
            graphicalPolygonPointArrayList.add(graphicalPolygonPoint);
            drawLineBetweenFirstAndLastPoint();
        }

        executed=true;
    }

    @Override
    public void unexecute() {
        graphicalPolygonPoint.removeAsChildrenFrom(pane);
        graphicalPolygonPointArrayList.remove(graphicalPolygonPoint);
        drawLineBetweenFirstAndLastPoint();
        executed=false;
    }

    private void drawLineBetweenFirstAndLastPoint() {
        int size=graphicalPolygonPointArrayList.size();
        if(size>0){
            GraphicalPolygonPoint firstPoint=graphicalPolygonPointArrayList.get(0);
            GraphicalPolygonPoint lastPoint=graphicalPolygonPointArrayList.get(size-1);
            firstPoint.getSide().setEndX(lastPoint.getX());
            firstPoint.getSide().setEndY(lastPoint.getY());
            if(size<3){
                firstPoint.getSide().setVisible(false);
            }else{
                firstPoint.getSide().setVisible(true);
            }
        }
    }


}
