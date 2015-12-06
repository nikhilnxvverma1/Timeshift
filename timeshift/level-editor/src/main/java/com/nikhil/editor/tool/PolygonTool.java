package com.nikhil.editor.tool;

import com.nikhil.command.AddPolygon;
import com.nikhil.command.AddPolygonPoint;
import com.nikhil.command.Command;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.bezier.GraphicalPolygonPoint;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class PolygonTool extends BaseTool{

    private static final double CLOSE_ENOUGH_DISTANCE_FOR_COINCIDE=5;

    private ArrayList<GraphicalPolygonPoint> graphicalPolygonPointArrayList;


    private AddPolygonPoint lastPolygonPointAddedCommand;

    public PolygonTool(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Pane pane=workspace.getCurrentComposition().getWorksheet();

        double x=xInWorksheet(mouseEvent);
        double y=yInWorksheet(mouseEvent);
        int size=graphicalPolygonPointArrayList.size();
        Line side=new Line();
        side.setStartX(x);
        side.setStartY(y);
        if(size>0){
            GraphicalPolygonPoint lastPoint=graphicalPolygonPointArrayList.get(size-1);

            side.setEndX(lastPoint.getX());
            side.setEndY(lastPoint.getY());

        }else{
            //for first point
            //end x ,y will be defined as the x,y of the last point
            side.getStrokeDashArray().add(GraphicalPolygonPoint.JOINING_LINE_DASH);
        }
        GraphicalPolygonPoint graphicalPolygonPoint=new GraphicalPolygonPoint(x,y,side);
        graphicalPolygonPoint.addAsChildrenTo(pane);
        graphicalPolygonPointArrayList.add(graphicalPolygonPoint);
        size++;//manually incremented,(depends on last variable)
        lastPolygonPointAddedCommand =new AddPolygonPoint(pane, graphicalPolygonPoint, graphicalPolygonPointArrayList, true);

        //based on the size of the point list, color them red or green
        assistPolygonCreation(size);

    }

    private void assistPolygonCreation(int size) {


        GraphicalPolygonPoint firstPoint=graphicalPolygonPointArrayList.get(0);
        if(size<3){
            for(GraphicalPolygonPoint p:graphicalPolygonPointArrayList){
                p.getPointRect().setFill(GraphicalPolygonPoint.INCOMPLETE_POLYGON_POINT_COLOR);
            }

            if(firstPoint!=null){
                firstPoint.getSide().setVisible(false);
            }
        }else{
            for(GraphicalPolygonPoint p:graphicalPolygonPointArrayList){
                p.getPointRect().setFill(GraphicalPolygonPoint.COMPLETE_POLYGON_POINT_COLOR);
            }
            firstPoint.getSide().setVisible(true);//first point will never be null
        }

        if (size>0) {
            GraphicalPolygonPoint lastPoint=graphicalPolygonPointArrayList.get(size-1);
            firstPoint.getSide().setEndX(lastPoint.getX());
            firstPoint.getSide().setEndY(lastPoint.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

        //drag the last added polygon point around

        int size=graphicalPolygonPointArrayList.size();
        double x=xInWorksheet(mouseEvent);
        double y=yInWorksheet(mouseEvent);

        if(size>0) {
            GraphicalPolygonPoint lastPoint = graphicalPolygonPointArrayList.get(size -1);
            lastPoint.setX(x);
            lastPoint.setY(y);

            //mind the joining line
            GraphicalPolygonPoint firstPoint=graphicalPolygonPointArrayList.get(0);
            firstPoint.getSide().setEndX(x);
            firstPoint.getSide().setEndY(y);
        }
    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {

        Command command;
        //last point was added coincides with first point
        if(lastPointAddedCoincidesWithFirst()){
            //last point is redundant in the circular list of polygon points,
            // therefore remove it along with its views
            int size=graphicalPolygonPointArrayList.size();
            GraphicalPolygonPoint lastPolygonPoint=graphicalPolygonPointArrayList.get(size-1);
            graphicalPolygonPointArrayList.remove(lastPolygonPoint);
            lastPolygonPoint.removeAsChildrenFrom(workspace.getCurrentComposition().getWorksheet());

            //make the polygon
            command = createAddPolygonCommand();
            //at this point,refresh tool for a new polygon
            resetTool();
        }else{
            //else only add a point to the polygon list
            command= lastPolygonPointAddedCommand;
        }
        workspace.pushCommand(command);
        return command;
    }

    private AddPolygon createAddPolygonCommand() {
        PolygonView polygonView=createPolygonView();
        PolygonViewController polygonViewController=new PolygonViewController(workspace.getCurrentComposition(),polygonView);
        return new AddPolygon(polygonViewController,graphicalPolygonPointArrayList);
    }

    /**
     * This method returns false if size of list is less than 3
     * @return true if the distance between the first and last polygon point
     * is too less,false otherwise
     */
    private boolean lastPointAddedCoincidesWithFirst(){

        int size=graphicalPolygonPointArrayList.size();
        if(size<3){
            return false;
        }
        GraphicalPolygonPoint lastPolygonPoint=graphicalPolygonPointArrayList.get(size-1);
        GraphicalPolygonPoint firstPolygonPoint=graphicalPolygonPointArrayList.get(0);
        if(lastPolygonPoint.distanceFrom(firstPolygonPoint)<=CLOSE_ENOUGH_DISTANCE_FOR_COINCIDE){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void toolAppointed(Tool lastSelectedTool) {
        resetTool();
    }

    private void resetTool() {
        //create a new graphical polygon point list
        graphicalPolygonPointArrayList=new ArrayList<>();
        lastPolygonPointAddedCommand=null;
    }

    @Override
    public void toolDismissed(Tool newToolSelected) {

        //add a polygon only if there are more than 3 points
        if(graphicalPolygonPointArrayList.size()>=3){
            workspace.pushCommand(createAddPolygonCommand());
        }else{
            //remove the graphics from the view
            removeGraphicalPolygonPointsFromView();
        }
    }

    private void removeGraphicalPolygonPointsFromView(){
        for(GraphicalPolygonPoint graphicalPolygonPoint: graphicalPolygonPointArrayList){
            graphicalPolygonPoint.removeAsChildrenFrom(workspace.getCurrentComposition().getWorksheet());
        }
    }

    private PolygonView createPolygonView() {

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

    @Override
    public ToolType getToolType() {
        return ToolType.POLYGON;
    }
}
