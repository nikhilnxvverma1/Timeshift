package com.nikhil.view.bezier;

import com.nikhil.util.modal.UtilPoint;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

/**
 * Bezier point that allows graphical manipulation through mouse press-drag-release events.
 * Created by NikhilVerma on 26/11/15.
 */
public class InteractiveBezierPoint extends GraphicalBezierPoint{

    //dirty variables needed for a press-drag-release event handling.
    //Only one instance of this class uses them at a time
    private static double lastX, lastY;
    private BezierPointInteraction delegate;

    public InteractiveBezierPoint(UtilPoint anchorPoint,BezierPointInteraction delegate) {
        super(new UtilPoint(anchorPoint),new CubicCurve());
        cubicCurve.setLayoutX(anchorPoint.getX());
        cubicCurve.setLayoutY(anchorPoint.getY());
        cubicCurve.setStartX(0);
        cubicCurve.setStartY(0);
        cubicCurve.setStroke(Color.BLUE);
        cubicCurve.setFill(null);
        this.delegate = delegate;
        addEventHandlers();
    }
    
    private void addEventHandlers(){
        anchorPointRect.addEventHandler(MouseEvent.MOUSE_PRESSED,this::anchorPointPressed);
        anchorPointRect.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::anchorPointDragged);
        anchorPointRect.addEventHandler(MouseEvent.MOUSE_RELEASED,this::anchorPointReleased);
        anchorPointRect.setCursor(Cursor.HAND);

        previousControlPointCircle.addEventHandler(MouseEvent.MOUSE_PRESSED, this::previousControlPointPressed);
        previousControlPointCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::previousControlPointDragged);
        previousControlPointCircle.addEventHandler(MouseEvent.MOUSE_RELEASED,this::previousControlPointReleased);
        previousControlPointCircle.setCursor(Cursor.HAND);

        nextControlPointCircle.addEventHandler(MouseEvent.MOUSE_PRESSED,this::nextControlPointPressed);
        nextControlPointCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this::nextControlPointDragged);
        nextControlPointCircle.addEventHandler(MouseEvent.MOUSE_RELEASED,this::nextControlPointReleased);
        nextControlPointCircle.setCursor(Cursor.HAND);

    }

    //=============================================================================================
    //Mouse press events
    //=============================================================================================

    private void anchorPointPressed(MouseEvent mouseEvent) {
        Point2D point2D = anchorPointRect.localToParent(mouseEvent.getX(), mouseEvent.getY());
        lastX =point2D.getX();
        lastY =point2D.getY();
        notifyDelegateAboutMousePress(mouseEvent);
        mouseEvent.consume();
    }

    private void previousControlPointPressed(MouseEvent mouseEvent) {
        Point2D point2D = previousControlPointCircle.localToParent(mouseEvent.getX(), mouseEvent.getY());
        lastX =point2D.getX();
        lastY =point2D.getY();
        notifyDelegateAboutMousePress(mouseEvent);
        mouseEvent.consume();
    }

    private void nextControlPointPressed(MouseEvent mouseEvent) {
        Point2D point2D = nextControlPointCircle.localToParent(mouseEvent.getX(), mouseEvent.getY());
        lastX =point2D.getX();
        lastY =point2D.getY();
        notifyDelegateAboutMousePress(mouseEvent);
        mouseEvent.consume();
    }

    private void notifyDelegateAboutMousePress(MouseEvent mouseEvent){
        if(delegate!=null){
            delegate.bezierPointBeganChanging(this,mouseEvent);
        }
    }

    //=============================================================================================
    //Mouse Drag events
    //=============================================================================================

    private void anchorPointDragged(MouseEvent mouseEvent) {
        Point2D point2D = anchorPointRect.localToParent(mouseEvent.getX(), mouseEvent.getY());
        double dx=point2D.getX()- lastX;
        double dy=point2D.getY()- lastY;

        if(mouseEvent.isShiftDown()){
            this.extendPreviousControlPoint(dx, dy);
        }else{
            lastX=point2D.getX();
            lastY=point2D.getY();

            UtilPoint movedPoint = anchorPoint.add(new UtilPoint(dx, dy));
            this.setAnchorPoint(movedPoint);
        }
        notifyDelegateAboutMouseDrag(mouseEvent);
        mouseEvent.consume();
    }

    private void previousControlPointDragged(MouseEvent mouseEvent) {
        Point2D point2D = previousControlPointCircle.localToParent(mouseEvent.getX(), mouseEvent.getY());
        double dx=point2D.getX()- lastX;
        double dy=point2D.getY()- lastY;

        lastX=point2D.getX();
        lastY=point2D.getY();
        UtilPoint movedPoint=controlPointWithPrevious.add(new UtilPoint(dx,dy));
        if(mouseEvent.isMetaDown()){
            this.setControlPointWithPrevious(movedPoint);
        }else{
            this.extendPreviousControlPoint(controlPointWithPrevious.getX() + dx, controlPointWithPrevious.getY() + dy);
        }
        notifyDelegateAboutMouseDrag(mouseEvent);
        mouseEvent.consume();
    }

    private void nextControlPointDragged(MouseEvent mouseEvent) {
        Point2D point2D = nextControlPointCircle.localToParent(mouseEvent.getX(), mouseEvent.getY());
        double dx=point2D.getX()- lastX;
        double dy=point2D.getY()- lastY;

        lastX=point2D.getX();
        lastY=point2D.getY();
        UtilPoint movedPoint=controlPointWithNext.add(new UtilPoint(dx,dy));
        if(mouseEvent.isMetaDown()){
            this.setControlPointWithNext(movedPoint);
        }else{
            this.extendNextControlPoint(controlPointWithNext.getX() + dx, controlPointWithNext.getY() + dy);
        }
        notifyDelegateAboutMouseDrag(mouseEvent);
        mouseEvent.consume();
    }

    private void notifyDelegateAboutMouseDrag(MouseEvent mouseEvent){
        if(delegate!=null){
            delegate.bezierPointChanging(this, mouseEvent);
        }
    }

    //=============================================================================================
    //Mouse Released events
    //=============================================================================================

    private void anchorPointReleased(MouseEvent mouseEvent) {
        notifyDelegateAboutMouseRelease(mouseEvent);
        mouseEvent.consume();
    }

    private void previousControlPointReleased(MouseEvent mouseEvent) {
        notifyDelegateAboutMouseRelease(mouseEvent);
    }

    private void nextControlPointReleased(MouseEvent mouseEvent) {
        notifyDelegateAboutMouseRelease(mouseEvent);
    }

    private void notifyDelegateAboutMouseRelease(MouseEvent mouseEvent){
        if(delegate!=null){
            delegate.bezierPointFinishedChanging(this,mouseEvent);
        }
    }
}
