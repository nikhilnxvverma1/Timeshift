package com.nikhil.editor.tool;

import com.nikhil.command.AddBezierPoint;
import com.nikhil.command.Command;
import com.nikhil.logging.Logger;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.travelpath.LinkPointView;
import com.nikhil.view.modelview.GraphicalBezierPoint;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

import java.util.ArrayList;

/**
 * Created by NikhilVerma on 24/08/15.
 */
public class PenTool extends BaseTool{



    private Pane pane;
    private ArrayList<GraphicalBezierPoint> bezierPoints=new ArrayList<GraphicalBezierPoint>();
    private AddBezierPoint lastCommand;

    private LinkPointView lastLinkPointView=null;

    public PenTool(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //instantiate new graphical bezier point
        double x=mouseEvent.getX();
        double y=mouseEvent.getY();
        Logger.log("bezier point x,y " + x+","+y);


        int size = bezierPoints.size();
        CubicCurve cubicCurve = null;
        if(size>0){
            GraphicalBezierPoint lastGraphicalBezierPoint= bezierPoints.get(size-1);
            cubicCurve = new CubicCurve();
            cubicCurve.setFill(null);
            cubicCurve.setStroke(Color.BLACK);
            cubicCurve.setLayoutX(x);
            cubicCurve.setLayoutY(y);
            cubicCurve.setStartX(0);
            cubicCurve.setStartY(0);
            cubicCurve.setControlX1(0);
            cubicCurve.setControlY1(0);
            //end x,y are relative to layout x,y
            float lastX = (float) lastGraphicalBezierPoint.getAnchorPoint().getX();
            cubicCurve.setEndX(lastX -x);
            float lastY = (float) lastGraphicalBezierPoint.getAnchorPoint().getY();
            cubicCurve.setEndY(lastY -y);
            //control points are absolute
            cubicCurve.setControlX2((lastGraphicalBezierPoint.getControlPointWithNext().getX() + lastX)-x);
            cubicCurve.setControlY2((lastGraphicalBezierPoint.getControlPointWithNext().getY()+lastY)-y);

            //side check if last graphical bezier point is the fist point,then hide its redundant control point with previous graphics
            if(size==1){
                lastGraphicalBezierPoint.getPreviousControlPointCircle().setVisible(false);
                lastGraphicalBezierPoint.getPreviousControlLine().setVisible(false);
            }
        }//no cubic curve for first node

        GraphicalBezierPoint graphicalBezierPoint=new GraphicalBezierPoint(new UtilPoint(x,y), cubicCurve);
        graphicalBezierPoint.addAsChildrenTo(pane);
        bezierPoints.add(graphicalBezierPoint);
        lastCommand=new AddBezierPoint(pane,graphicalBezierPoint,bezierPoints,true);

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        //get the last bezier point in the list
        int size = bezierPoints.size();
        GraphicalBezierPoint lastGraphicalBezierPoint= bezierPoints.get(size - 1);
        //get relative drag from initial point
        double x=lastGraphicalBezierPoint.getAnchorPoint().getX()-mouseEvent.getX();
        double y=lastGraphicalBezierPoint.getAnchorPoint().getY()-mouseEvent.getY();
        Logger.log("dragged at "+x+","+y);
        lastGraphicalBezierPoint.extendControlPointsUsing(x, y);

    }

    @Override
    public Command mouseReleased(MouseEvent mouseEvent) {
        return lastCommand;
    }

    @Override
    public ToolType getToolType() {
        return ToolType.PEN;
    }
}
