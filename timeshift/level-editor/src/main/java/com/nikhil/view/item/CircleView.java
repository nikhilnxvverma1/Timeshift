package com.nikhil.view.item;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Customizable view for circle that uses Path to display the
 * circle item
 * Created by NikhilVerma on 02/09/15.
 */
public class CircleView {//TODO consider extending Path,then it will be much appropriate to call it a view

    //=============================================================================================
    //Circle Properties
    //=============================================================================================

    protected double scale;
    protected double rotation;
    protected double translationX;
    protected double translationY;
    protected double anchorPointX;
    protected double anchorPointY;

    protected double startAngle;
    protected double endAngle;
    protected double innerRadius;
    protected double outerRadius;

    //=============================================================================================
    //UI Componenets
    //=============================================================================================

    protected Path path;
    protected MoveTo moveToA;
    protected ArcTo arcToB;
    protected LineTo lineToC;
    protected ArcTo arcToD;

    public CircleView() {
        this(1,0,0,0,0.5,0.5,0,1,0,359);
    }

    public CircleView(CircleView circleView){
        this(circleView.scale,circleView.rotation,circleView.translationX,circleView.translationY,circleView.anchorPointX,circleView.anchorPointY,
        circleView.innerRadius,circleView.outerRadius,circleView.startAngle,circleView.endAngle);
    }

    public CircleView(double scale, double rotation, double translationX, double translationY,
                      double anchorPointX, double anchorPointY, double innerRadius,
                      double outerRadius, double startAngle, double endAngle) {
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.endAngle = endAngle;
        this.startAngle = startAngle;
        this.anchorPointY = anchorPointY;
        this.anchorPointX = anchorPointX;
        this.translationY = translationY;
        this.translationX = translationX;
        this.rotation = rotation;
        this.scale = scale;
        initializeView();
        updateView();
    }

    private void initializeView() {
        path=new Path();
        //TODO define constants
        path.setStroke(null);
        path.setFill(Color.ORANGE);
        moveToA=new MoveTo();
        arcToB=new ArcTo();
        lineToC=new LineTo();
        arcToD=new ArcTo();
        path.getElements().add(moveToA);
        path.getElements().add(arcToB);
        path.getElements().add(lineToC);
        path.getElements().add(arcToD);
        path.getElements().add(new ClosePath());
//        path.getElements().add(lineBackToA);//not reqd
    }

    private double getXBasisAnchor(){
        return translationX+2*outerRadius*anchorPointX;
    }

    private double getYBasisAnchor(){
        return translationY+2*outerRadius*anchorPointY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        path.setScaleX(scale);
        path.setScaleY(scale);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        path.setRotate(rotation);//might be in radians
    }

    public double getTranslationX() {
        return translationX;
    }

    public void setTranslationX(double translationX) {
        this.translationX = translationX;
        path.setLayoutX(getXBasisAnchor());
    }

    public double getTranslationY() {
        return translationY;
    }

    public void setTranslationY(double translationY) {
        this.translationY = translationY;
        path.setTranslateY(getYBasisAnchor());
    }

    public double getAnchorPointX() {
        return anchorPointX;
    }

    public void setAnchorPointX(double anchorPointX) {
        this.anchorPointX = anchorPointX;
        path.setTranslateX(getXBasisAnchor());
    }

    public double getAnchorPointY() {
        return anchorPointY;
    }

    public void setAnchorPointY(double anchorPointY) {
        this.anchorPointY = anchorPointY;
        path.setTranslateY(getYBasisAnchor());
    }

    public double getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        updateView();
    }

    public double getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(double endAngle) {
        this.endAngle = endAngle;
        updateView();
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius = innerRadius;
        updateView();
    }

    public double getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(double outerRadius) {
        this.outerRadius = outerRadius;
        updateView();
    }

    public Path getPath() {
        return path;
    }

    /**
     * Updates the view based on currently set
     * properties
     */
    public void updateView(){
        //set internal properties
        double startAngleR = Math.toRadians(startAngle);
        double endAngleR = Math.toRadians(endAngle);

        double ax=Math.cos(startAngleR)*innerRadius;
        double ay=Math.sin(startAngleR)*innerRadius;
        moveToA.setX(ax);
        moveToA.setY(ay);

        double bx=Math.cos(endAngleR)*innerRadius;
        double by=Math.sin(endAngleR)*innerRadius;
        arcToB.setRadiusX(innerRadius);
        arcToB.setRadiusY(innerRadius);
        arcToB.setSweepFlag(true);
        arcToB.setLargeArcFlag(endAngle-startAngle>=180);

        arcToB.setX(bx);
        arcToB.setY(by);

        double cx=Math.cos(endAngleR)*outerRadius;
        double cy=Math.sin(endAngleR)*outerRadius;
        lineToC.setX(cx);
        lineToC.setY(cy);

        double dx=Math.cos(startAngleR)*outerRadius;
        double dy=Math.sin(startAngleR)*outerRadius;
        arcToD.setX(dx);
        arcToD.setY(dy);
        arcToD.setRadiusX(outerRadius);
        arcToD.setRadiusY(outerRadius);
        arcToD.setSweepFlag(false);
        arcToD.setLargeArcFlag(endAngle-startAngle>=180);

//        lineBackToA.setX(ax);
//        lineBackToA.setY(ay);

        //TODO still need to take anchor point into account

        //set general shape properties
        double width=(2*outerRadius);
        double height=(2*outerRadius);

        double x=translationX+width*anchorPointX;
        double y=translationY+height*anchorPointY;
        path.setLayoutX(translationX);
        path.setLayoutY(translationY);
        path.setScaleX(scale);
        path.setScaleY(scale);
        path.setRotate(rotation);
    }

    public void copyValuesFrom(CircleView circleView){
        scale=circleView.scale;
        rotation=circleView.rotation;
        translationX=circleView.translationX;
        translationY=circleView.translationY;
        anchorPointX=circleView.anchorPointX;
        anchorPointY=circleView.anchorPointY;

        innerRadius=circleView.innerRadius;
        outerRadius=circleView.outerRadius;
        startAngle=circleView.startAngle;
        endAngle=circleView.endAngle;
        updateView();

        //TODO: test out these methods and use these instead
//        setScale(circleView.scale);
//        setRotation(circleView.rotation);
//        setTranslationX(circleView.translationX);
//        setTranslationY(circleView.translationY);
//        setAnchorPointX(circleView.anchorPointX);
//        setAnchorPointY(circleView.anchorPointY);
//
//        setInnerRadius(circleView.innerRadius);
//        setOuterRadius(circleView.outerRadius);
//        setStartAngle(circleView.startAngle);
//        setEndAngle(circleView.endAngle);
    }
}
