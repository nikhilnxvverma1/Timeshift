package com.nikhil.view.item.travelpath;

import com.nikhil.logging.Logger;
import com.nikhil.space.bezier.path.BezierPoint;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Created by NikhilVerma on 07/09/15.
 */
public class LinkPointView extends Group implements EventHandler<MouseEvent>{

    public static final double ANCHOR_SQUARE_WIDTH=12;
    public static final double CONTROL_CIRCLE_RADIUS=5;
    public static final double CONTROL_LINE_DASH=7;
    private static final Color ANCHOR_FILL= Color.BLUE;
    private static final Color CONTROL_FILL= Color.BLUE;

    private TravelPathView previous;
    private TravelPathView next;

    //=============================================================================================
    //Link point properties
    //=============================================================================================

    private double x;
    private double y;
    private double controlPointWithPreviousX;
    private double controlPointWithPreviousY;
    private double controlPointWithNextX;
    private double controlPointWithNextY;

    //=============================================================================================
    //UI components
    //=============================================================================================

    private Rectangle anchorSquare;
    private Circle previousControlCircle;
    private Line previousControlLine;
    private Line nextControlLine;
    private Circle nextControlCircle;

    public LinkPointView(LinkPointView linkPointView){
        this(linkPointView.x,linkPointView.y,
                linkPointView.controlPointWithPreviousX,linkPointView.controlPointWithPreviousY,
                linkPointView.controlPointWithNextX,linkPointView.controlPointWithNextY);
    }

    public LinkPointView(double x, double y) {
        this(x,y,0,0,0,0);
    }

    public LinkPointView(double x, double y, double controlPointWithPreviousX, double controlPointWithPreviousY,
                         double controlPointWithNextX, double controlPointWithNextY) {
        this.x = x;
        this.y = y;
        this.controlPointWithPreviousX = controlPointWithPreviousX;
        this.controlPointWithPreviousY = controlPointWithPreviousY;
        this.controlPointWithNextX = controlPointWithNextX;
        this.controlPointWithNextY = controlPointWithNextY;
        initializeView();
        updateView();
    }

    public TravelPathView getPrevious() {
        return previous;
    }

    public void setPrevious(TravelPathView previous) {
        this.previous = previous;
    }

    public TravelPathView getNext() {
        return next;
    }

    public void setNext(TravelPathView next) {
        this.next = next;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        updateView();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        updateView();
    }

    public double getControlPointWithPreviousX() {
        return controlPointWithPreviousX;
    }

    public void setControlPointWithPreviousX(double controlPointWithPreviousX) {
        this.controlPointWithPreviousX = controlPointWithPreviousX;
        updateView();
    }

    public double getControlPointWithPreviousY() {
        return controlPointWithPreviousY;
    }

    public void setControlPointWithPreviousY(double controlPointWithPreviousY) {
        this.controlPointWithPreviousY = controlPointWithPreviousY;
        updateView();
    }

    public double getControlPointWithNextX() {
        return controlPointWithNextX;
    }

    public void setControlPointWithNextX(double controlPointWithNextX) {
        this.controlPointWithNextX = controlPointWithNextX;
        updateView();
    }

    public double getControlPointWithNextY() {
        return controlPointWithNextY;
    }

    public void setControlPointWithNextY(double controlPointWithNextY) {
        this.controlPointWithNextY = controlPointWithNextY;
        updateView();
    }

    private void initializeView(){

        //anchor square
        anchorSquare=new Rectangle(ANCHOR_SQUARE_WIDTH,ANCHOR_SQUARE_WIDTH,ANCHOR_FILL);
        anchorSquare.setLayoutX(0);
        anchorSquare.setLayoutY(0);
        anchorSquare.addEventHandler(MouseEvent.MOUSE_PRESSED,this);
        anchorSquare.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);

        //set anchor squares anchor point in the center of the square
        anchorSquare.setTranslateX(-ANCHOR_SQUARE_WIDTH/2);
        anchorSquare.setTranslateY(-ANCHOR_SQUARE_WIDTH / 2);

        //previous control point circle
        previousControlCircle=new Circle(0,0,CONTROL_CIRCLE_RADIUS,CONTROL_FILL);
        previousControlCircle.setLayoutX(controlPointWithPreviousX);
        previousControlCircle.setLayoutY(controlPointWithPreviousY);
        previousControlCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);

        //previous control point line
        previousControlLine=new Line(0,0,controlPointWithPreviousX,controlPointWithPreviousY);
        previousControlLine.getStrokeDashArray().add(CONTROL_LINE_DASH);
        previousControlLine.setLayoutX(0);
        previousControlLine.setLayoutY(0);

        //next control point circle
        nextControlCircle=new Circle(0,0,CONTROL_CIRCLE_RADIUS,CONTROL_FILL);
        nextControlCircle.setLayoutX(controlPointWithNextX);
        nextControlCircle.setLayoutY(controlPointWithNextY);
        nextControlCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);

        //next control point line
        nextControlLine=new Line(0,0,controlPointWithNextX,controlPointWithNextY);
        nextControlLine.getStrokeDashArray().add(CONTROL_LINE_DASH);
        nextControlLine.setLayoutX(0);
        nextControlLine.setLayoutY(0);

        //add all the elements in such an order so points come on top
        this.getChildren().addAll(previousControlLine,nextControlLine,previousControlCircle,nextControlCircle,anchorSquare);
    }
    
    private void updateView(){

        this.setLayoutX(x);
        this.setLayoutY(y);

        previousControlCircle.setLayoutX(controlPointWithPreviousX);
        previousControlCircle.setLayoutY(controlPointWithPreviousY);
        
        previousControlLine.setEndX(controlPointWithPreviousX);
        previousControlLine.setEndY(controlPointWithPreviousY);
        
        //hide previous control point if it is zero
        if(isZero(controlPointWithPreviousX,controlPointWithPreviousY)||previous==null){
            showPreviousControlPointGraphics(false);
        }else{
            showPreviousControlPointGraphics(true);
        }
        
        nextControlCircle.setLayoutX(controlPointWithNextX);
        nextControlCircle.setLayoutY(controlPointWithNextY);
        
        nextControlLine.setEndX(controlPointWithNextX);
        nextControlLine.setEndY(controlPointWithNextY);

        //hide next control point if it is zero
        if(isZero(controlPointWithNextX,controlPointWithNextY)){
            showNextControlPointGraphics(false);
        }else{
            showNextControlPointGraphics(true);
        }

        //update the travel paths on either sides (if they exist)
        if(previous!=null){
            previous.linkPointChanged(this);
        }
        if(next!=null){
            next.linkPointChanged(this);
        }
    }

    public void showPreviousControlPointGraphics(boolean visible){
        previousControlCircle.setVisible(visible);
        previousControlLine.setVisible(visible);
    }

    public void showNextControlPointGraphics(boolean visible){
        nextControlCircle.setVisible(visible);
        nextControlLine.setVisible(visible);
    }

    public void showControlPointGraphics(boolean visible){
        showPreviousControlPointGraphics(visible);
        showNextControlPointGraphics(visible);
    }
    
    private boolean isZero(double x,double y){
        if(x==0&&y==0){
            return true;
        }
        return false;
    }

    public void extendNextControlPoints(double x, double y){
        controlPointWithPreviousX=-x;
        controlPointWithPreviousY=-y;
        //inverted coordinates will be set on the control point with next
        controlPointWithNextX=x;
        controlPointWithNextY=y;
        updateView();
    }

    private void extendPreviousControlPoint(double x,double y){
        controlPointWithPreviousX=x;
        controlPointWithPreviousY=y;
        //inverted coordinates will be set on the control point with previous
        controlPointWithNextX=-x;
        controlPointWithNextY=-y;
        updateView();
    }

    //=============================================================================================
    //Alt list manipulation TODO get rid of static method, its repeated in ViewAltList
    //=============================================================================================

    /**
     * removes the specified link point view from the alt list by severing the connection from
     * the travel path views on either sides  of the link point(if they exist) from the main list.
     * This splits the list into two lists.If required caller should keep track of the tail of
     * the first list before making the call.
     * The connection between this link point view and the travel paths view on either sides
     * still exists.
     * @return the head of the second resulting list created after the split.null if this is the last node.
     */
    public LinkPointView removeFromList(){

        //sever connection from the previous travel path
        if(previous!=null){
            previous.getPrevious().setNext(null);
            previous.setPrevious(null);
        }

        LinkPointView headOfSecondListAfterSplit=null;
        //sever connection from the next travel path
        if(next!=null){
            headOfSecondListAfterSplit=next.getNext();
            next.getNext().setPrevious(null);
            next.setNext(null);
        }
        return headOfSecondListAfterSplit;
    }

    /**
     * appends a link point after this link point to the alt list
     * by inserting a travel path view between the this
     * link point and the one that needs to be appended
     * @param linkPointView the link point to append after this
     */
    public void append(LinkPointView linkPointView){
        ViewAltList.buildAndInsertTravelPathViewBetween(this, linkPointView);
    }

    /**
     * builds an alt list of link points and travel paths from a list of bezier points.
     * @param bezierPointList list of bezier points used in making up the alt list
     * @return the first link point view created
     */

    public static LinkPointView buildTravelPathLinkPointViewList(List<BezierPoint> bezierPointList){
        return buildTravelPathLinkPointViewList(bezierPointList, null);
    }

    /**
     * builds an alt list of link points and travel paths from a list of bezier points.
     * the list build will be appended to the last argument specified
     * @param bezierPointList list of bezier points used in making up the alt list
     * @param appendAfter the link point after which this list should be appended. if null, this
     *                    list will begin from the first node created
     * @return the first link point view created
     */
    public static LinkPointView buildTravelPathLinkPointViewList(List<BezierPoint> bezierPointList,
                                                                 LinkPointView appendAfter){
        LinkPointView firstLinkPointView=null;
        LinkPointView lastLinkPointView=appendAfter;

        //for each bezier point
        for(BezierPoint p:bezierPointList){

            //create a new link point view for this point
            LinkPointView currentLinkPointView = getLinkPointViewFromBezierPoint(p);

            //sandwich a travel path view between this link point and the last link point
            if(lastLinkPointView!=null){
                buildAndInsertTravelPathViewBetween(lastLinkPointView, currentLinkPointView);
            }else{
                lastLinkPointView=currentLinkPointView;
            }

            //keep a reference to the first link point created
            if(firstLinkPointView==null){
                firstLinkPointView=currentLinkPointView;
            }
        }

        return firstLinkPointView;
    }

    /**
     * creates and inserts a travel path view between two link point views. this helps in
     * connecting two link point views as the nodes of the list alternate between link point
     * and travel path
     * @param firstLinkPointView first link point of the travel path
     * @param secondLinkPointView second link point of the travel path
     * @return the travel path view created
     */
    public static TravelPathView buildAndInsertTravelPathViewBetween(LinkPointView firstLinkPointView, LinkPointView secondLinkPointView) {
        TravelPathView travelPathView=new TravelPathView(firstLinkPointView, secondLinkPointView);
        firstLinkPointView.setNext(travelPathView);
        secondLinkPointView.setPrevious(travelPathView);
        return travelPathView;
    }

    /**
     * creates a link point using a bezier point
     * @param p the bezier point containing information about anchor point and control point
     * @return the link point created
     */
    public static LinkPointView getLinkPointViewFromBezierPoint(BezierPoint p) {
        return new LinkPointView(p.getAnchorPoint().getX(),p.getAnchorPoint().getY(),
                p.getControlPointWithPrevious().getX(),p.getControlPointWithPrevious().getY(),
                p.getControlPointWithNext().getX(),p.getControlPointWithNext().getY());
    }

    /**
     * appends a link point to the alt list by inserting a travel path view between the last
     * link point and the one that just got created
     * @param bezierPoint the bezier point containing information about anchor point and control point
     * @param lastLinkLinkPointView the link point after which this list should be appended. if null, the
     *                              created link point will not be connected to anything.
     * @return the link point view created that needed to be appended.
     */
    public static LinkPointView appendToTravelPathLinkPointViewList(BezierPoint bezierPoint,LinkPointView lastLinkLinkPointView){
        LinkPointView linkPointViewToAppend=getLinkPointViewFromBezierPoint(bezierPoint);
        if(lastLinkLinkPointView!=null){
            buildAndInsertTravelPathViewBetween(lastLinkLinkPointView, linkPointViewToAppend);
        }
        return linkPointViewToAppend;
    }

    /**
     * appends a link point to the alt list by inserting a travel path view between the last
     * link point and the one that needs to be appended
     * @param lastLinkLinkPointView the link point after which this list should be appended. if null, the
     *                              link point will not be connected to anything.
     * @return the same link point view that got appended
     */
    public static LinkPointView appendToTravelPathLinkPointViewList(LinkPointView linkPointViewToAppend,LinkPointView lastLinkLinkPointView){
        if(lastLinkLinkPointView!=null){
            buildAndInsertTravelPathViewBetween(lastLinkLinkPointView, linkPointViewToAppend);
        }
        return linkPointViewToAppend;
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==anchorSquare){
            if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
                //hide control points of other link points
                showControlPointGraphicsOfSiblings(false);
                showControlPointGraphics(true);//only show for this one
            }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
                tweakAnchorPoint(event);
            }
        }else if(event.getTarget()==previousControlCircle){
            tweakPreviousControlPoint(event);
        }else if(event.getTarget()==nextControlCircle){
            tweakNextControlPoint(event);
        }
    }

    public void tweakAnchorPoint(MouseEvent mouseEvent){
        double dx=mouseEvent.getX();
        double dy=mouseEvent.getY();
        if(mouseEvent.isShiftDown()){
            extendNextControlPoints(dx,dy);
        }else{
            setX(this.x+dx);
            setY(this.y+dy);
        }
        mouseEvent.consume();
    }

    public void tweakPreviousControlPoint(MouseEvent mouseEvent){

        double newControlPointX = controlPointWithPreviousX + mouseEvent.getX();
        double newControlPointY = controlPointWithPreviousY + mouseEvent.getY();
        if(mouseEvent.isControlDown()){
            controlPointWithPreviousX=newControlPointX;
            controlPointWithPreviousY=newControlPointY;
            updateView();
        }else{
            extendPreviousControlPoint(newControlPointX, newControlPointY);
        }
        mouseEvent.consume();

    }

    public void tweakNextControlPoint(MouseEvent mouseEvent){

        double newControlPointX = controlPointWithNextX + mouseEvent.getX();
        double newControlPointY = controlPointWithNextY + mouseEvent.getY();
        if(mouseEvent.isControlDown()){
            controlPointWithNextX=newControlPointX;
            controlPointWithNextY=newControlPointY;
            updateView();
        }else{
            extendNextControlPoints(newControlPointX, newControlPointY);
        }
        mouseEvent.consume();
    }

    public void showControlPointGraphicsOfSiblings(boolean visible){
        LinkPointView t=this;
        //go back and toggle visibility
        while (t != null) {
            t.showControlPointGraphics(visible);
            if(t.getPrevious()!=null){
                t=t.getPrevious().getPrevious();
            }else {
                t=null;
            }
        }
        t=this;
        //go forward and toggle visibility
        while (t != null) {
            t.showControlPointGraphics(visible);
            if(t.getNext()!=null){
                t=t.getNext().getNext();
            }else {
                t=null;
            }
        }
    }

    private boolean areControlPointGraphicsVisible(){
        return previousControlCircle.isVisible()||nextControlCircle.isVisible();
    }
}
