package com.nikhil.view.item;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Created by NikhilVerma on 04/09/15.
 */
public class ParallelogramView extends Path {

    //=============================================================================================
    //Parallelogram Properties
    //=============================================================================================

    private double scale;
    private double rotation;
    private double translationX;
    private double translationY;
    private double anchorPointX;
    private double anchorPointY;

    private double width;
    private double height;
    private double swayAngle;

    //=============================================================================================
    //UI components
    //=============================================================================================

    protected MoveTo moveToA;
    protected LineTo lineToB;
    protected LineTo lineToC;
    protected LineTo lineToD;


    public ParallelogramView(double width, double height, double swayAngle, double scale, double rotation, double translationX, double translationY, double anchorPointX, double anchorPointY) {
        this.swayAngle = swayAngle;
        this.height = height;
        this.width = width;
        this.anchorPointY = anchorPointY;
        this.anchorPointX = anchorPointX;
        this.translationY = translationY;
        this.translationX = translationX;
        this.rotation = rotation;
        this.scale = scale;
        initializeView();
        updateView();
    }

    public ParallelogramView(ParallelogramView parallelogramView) {
        this(parallelogramView.width,parallelogramView.height,parallelogramView.swayAngle,parallelogramView.scale,parallelogramView.rotation
        ,parallelogramView.translationX,parallelogramView.translationY,parallelogramView.anchorPointX,parallelogramView.anchorPointY);
    }

    public double getScale() {
        return scale;
    }

    public double getRotation() {
        return rotation;
    }

    public double getTranslationX() {
        return translationX;
    }

    public double getTranslationY() {
        return translationY;
    }

    public double getAnchorPointX() {
        return anchorPointX;
    }

    public double getAnchorPointY() {
        return anchorPointY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getSwayAngle() {
        return swayAngle;
    }

    public void setScale(double scale) {
        this.scale = scale;
        updateView();
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        updateView();
    }

    public void setTranslationX(double translationX) {
        this.translationX = translationX;
        updateView();
    }

    public void setTranslationY(double translationY) {
        this.translationY = translationY;
        updateView();
    }

    public void setAnchorPointX(double anchorPointX) {
        this.anchorPointX = anchorPointX;
        updateView();
    }

    public void setAnchorPointY(double anchorPointY) {
        this.anchorPointY = anchorPointY;
        updateView();
    }

    public void setWidth(double width) {
        this.width = width;
        updateView();
    }

    public void setHeight(double height) {
        this.height = height;
        updateView();
    }

    public void setSwayAngle(double swayAngle) {
        this.swayAngle = swayAngle;
        updateView();
    }

    private void initializeView(){
        this.setFill(Color.ORANGE);
        this.setStroke(null);
        moveToA=new MoveTo();
        lineToB=new LineTo();
        lineToC=new LineTo();
        lineToD=new LineTo();
        this.getElements().addAll(moveToA,lineToB,lineToC,lineToD,new ClosePath());
    }

    /**
     * neverminding the y axis going downwards
     * @return the lower left point on the parallelogram
     */
    public Point2D getLowerRight(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle))*(height/2);
        double ax= width / 2 + distanceFromMidX;
        double ay=boundingHeight/2;
        return new Point2D(ax,ay);
    }

    /**
     * neverminding the y axis going downwards
     * @return the lower right point on the parallelogram
     */
    public Point2D getLowerLeft(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle))*(height/2);
        double bx= -width / 2 + distanceFromMidX;
        double by=boundingHeight/2;
        return new Point2D(bx,by);
    }


    /**
     * neverminding the y axis going downwards
     * @return the upper left point on the parallelogram
     */
    public Point2D getUpperLeft(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle))*(height/2);
        double cx= -width / 2 - distanceFromMidX;
        double cy=-boundingHeight/2;
        return new Point2D(cx,cy);
    }


    /**
     * neverminding the y axis going downwards
     * @return the upper right point on the parallelogram
     */
    public Point2D getUpperRight(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle))*(height/2);
        double dx= width / 2 - distanceFromMidX;
        double dy=-boundingHeight/2;
        return new Point2D(dx,dy);
    }


    private void updateView(){

        Point2D a=getLowerLeft();
        moveToA.setX(a.getX());
        moveToA.setY(a.getY());

        Point2D b=getLowerRight();
        lineToB.setX(b.getX());
        lineToB.setY(b.getY());

        Point2D c=getUpperRight();
        lineToC.setX(c.getX());
        lineToC.setY(c.getY());

        Point2D d=getUpperLeft();
        lineToD.setX(d.getX());
        lineToD.setY(d.getY());

        //TODO take into account anchor point
        setLayoutX(translationX);
        setLayoutY(translationY);
//        setTranslateX(translationX);
//        setTranslateY(translationY);
        setRotate(rotation);
        setScaleY(scale);

    }

    /**
     * @return the absolute bounding height of the parallelogram
     */
    public double getBoundingHeight(){
        return Math.abs(Math.sin(Math.toRadians(swayAngle))*height);
    }

    /**
     * @return the absolute bounding width of the parallelogram
     */

    public double getBoundingWidth(){
        return width+Math.abs(2*Math.cos(Math.toRadians(swayAngle))*height);
    }
}
