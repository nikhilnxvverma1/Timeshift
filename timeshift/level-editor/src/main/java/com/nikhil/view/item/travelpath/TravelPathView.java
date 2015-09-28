package com.nikhil.view.item.travelpath;

import com.nikhil.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Created by NikhilVerma on 07/09/15.
 */
public class TravelPathView extends Path implements EventHandler<MouseEvent>{

    private static final double TRAVEL_PATH_DASH=2;

    private LinkPointView previous;
    private LinkPointView next;

    //=============================================================================================
    //UI components
    //=============================================================================================

    private CubicCurveTo cubicCurveToNext;

    //TODO path validity list

    public TravelPathView(LinkPointView previous, LinkPointView next) {
        this.previous = previous;
        this.next = next;
        initializeView();
        updateView();
    }

    public TravelPathView(TravelPathView travelPathView) {
        this(travelPathView.previous,travelPathView.next);
    }

    public LinkPointView getPrevious() {
        return previous;
    }

    public void setPrevious(LinkPointView previous) {
        this.previous = previous;
    }

    public LinkPointView getNext() {
        return next;
    }

    public void setNext(LinkPointView next) {
        this.next = next;
    }

    public void linkPointChanged(LinkPointView linkPointView){
        if(linkPointView==next){
            updateView();
        }else if(linkPointView==previous){
            updateView();
        }
    }

    private void initializeView(){
        MoveTo startFromPrevious=new MoveTo(0,0);
        cubicCurveToNext = new CubicCurveTo();
        this.getElements().addAll(startFromPrevious,cubicCurveToNext);
        this.getStrokeDashArray().add(TRAVEL_PATH_DASH);
//        this.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);//doesn't work
        Logger.log("event added");
    }

    private void updateView(){
        if(next==null||previous==null){
            return;
        }
        //the layout x,y of the entire path will serve as
        // the previous anchor point,because cubicCurveToNext starts
        //starts from previous link point (MoveTo element before this)
        this.setLayoutX(previous.getX());
        this.setLayoutY(previous.getY());
        //set previous control points
        cubicCurveToNext.setControlX1(previous.getControlPointWithNextX());
        cubicCurveToNext.setControlY1(previous.getControlPointWithNextY());
        //set next link point relative to this previous link point
        double nextLinkPointX = next.getX()-previous.getX();
        double nextLinkPointY = next.getY()-previous.getY();

        //control point for next link point will be relative to the
        //values computed above
        double nextLinkPointControlX= nextLinkPointX + next.getControlPointWithPreviousX();
        double nextLinkPointControlY= nextLinkPointY + next.getControlPointWithPreviousY();
        cubicCurveToNext.setX(nextLinkPointX);
        cubicCurveToNext.setY(nextLinkPointY);
        cubicCurveToNext.setControlX2(nextLinkPointControlX);
        cubicCurveToNext.setControlY2(nextLinkPointControlY);

    }

    @Override
    public void handle(MouseEvent event) {
        Logger.log("Event fired");
        if(event.getTarget()==this){
            tweakTravelPathOnBothSides(event);
        }
    }

    //untested
    private void tweakTravelPathOnBothSides(MouseEvent mouseEvent){

        double dx=mouseEvent.getX();
        double dy=mouseEvent.getY();

        double px=previous.getX()+dx;
        double py=previous.getY()+dy;
        previous.setX(px);
        previous.setY(py);

        double nx=next.getX()+dx;
        double ny=next.getY()+dy;
        next.setX(nx);
        next.setY(ny);

        mouseEvent.consume();
    }
}
