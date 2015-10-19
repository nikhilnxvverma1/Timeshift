package com.nikhil.command;

import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.modelview.GraphicalPolygonPoint;
//import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 19/09/15.
 */
public class AddPolygon implements Command{

    private PolygonViewController polygonViewController;
    private List<GraphicalPolygonPoint> graphicalPolygonPointArrayList;


    public AddPolygon(PolygonViewController polygonViewController, List<GraphicalPolygonPoint> graphicalPolygonPointArrayList) {
        this.polygonViewController = polygonViewController;
        this.graphicalPolygonPointArrayList = graphicalPolygonPointArrayList;
    }

    @Override
    public void execute() {

        //remove all the graphical polygon points from the front end
        removeGraphicalPolygonPointsFromView();

        CompositionViewController compositionViewController = polygonViewController.getCompositionViewController();
        //and controller to the compositionViewController
        compositionViewController.addItemViewController(polygonViewController);
        //add the polygon and its gizmo to the view
        polygonViewController.addViewsToWorksheet();

        //add to timeline
        compositionViewController.addToTimelineSystem(polygonViewController);
    }

    @Override
    public void unexecute() {

        //remove the controller from the item view controller list
        CompositionViewController compositionViewController=polygonViewController.getCompositionViewController();
        compositionViewController.removeItemViewController(polygonViewController);
        //also remove the polygon views and gizmo from the worksheet
        polygonViewController.removeViewsFromWorksheet();

        //show the graphical helping outline again
        addGraphicalPolygonPointsToView();
        drawLineBetweenFirstAndLastPoint();

        //remove from timeline
        compositionViewController.removeFromTimelineSystem(polygonViewController);
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

    private void removeGraphicalPolygonPointsFromView(){
        Workspace workspace=polygonViewController.getCompositionViewController().getWorkspace();
        for(GraphicalPolygonPoint graphicalPolygonPoint: graphicalPolygonPointArrayList){
            graphicalPolygonPoint.removeAsChildrenFrom(workspace.getWorksheetPane());
        }
    }

    private void addGraphicalPolygonPointsToView(){
        Workspace workspace=polygonViewController.getCompositionViewController().getWorkspace();
        for(GraphicalPolygonPoint graphicalPolygonPoint: graphicalPolygonPointArrayList){
            graphicalPolygonPoint.addAsChildrenTo(workspace.getWorksheetPane());
        }
    }

    private PolygonView createPolygonView() {//TODO remove from here

        //find the average mid point of all graphical polygon points
        double sumX=0;
        double sumY=0;
        int totalPoints=0;//same as arraylist.size() ,kinda redundant
        for(GraphicalPolygonPoint graphicalPolygonPoint : graphicalPolygonPointArrayList){
            sumX+=graphicalPolygonPoint.getX();
            sumY+=graphicalPolygonPoint.getY();
            totalPoints++;
        }
        double x=sumX/totalPoints;
        double y=sumY/totalPoints;

        //create a list of polygon point using the list of graphical polygon points
        List<UtilPoint> polygonPoints=new ArrayList<UtilPoint>(totalPoints);
        for(GraphicalPolygonPoint graphicalPolygonPoint : graphicalPolygonPointArrayList){

            double relativeX = graphicalPolygonPoint.getX()-x;
            double relativeY = graphicalPolygonPoint.getY()-y;
            UtilPoint polygonPoint=new UtilPoint(relativeX, relativeY);
            polygonPoints.add(polygonPoint);
        }

        //use this list to create the polygon view
        PolygonView polygonView=new PolygonView(polygonPoints,x,y,0,1);

        return  polygonView;
    }
}
