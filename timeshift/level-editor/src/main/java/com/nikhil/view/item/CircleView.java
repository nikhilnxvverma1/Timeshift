package com.nikhil.view.item;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Customizable view for circle that uses Path to display the
 * circle item
 * Created by NikhilVerma on 02/09/15.
 */
public class CircleView extends Path{

    //=============================================================================================
    //Circle Properties
    //=============================================================================================

    protected double startAngle;
    protected double endAngle;
    protected double innerRadius;
    protected double outerRadius;

    //=============================================================================================
    //UI Components
    //=============================================================================================
    
    protected MoveTo moveToA;
    protected ArcTo arcToB;
    protected LineTo lineToC;
    protected ArcTo arcToD;

    public CircleView() {
        this(0,1,0,359);
    }

    public CircleView(CircleView circleView){
        this(circleView.innerRadius,circleView.outerRadius,circleView.startAngle,circleView.endAngle);
    }

    public CircleView(double innerRadius,double outerRadius, double startAngle, double endAngle) {
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.endAngle = endAngle;
        this.startAngle = startAngle;
        initView();
        updateView();
    }

    private void initView() {
        this.setStroke(null);
        this.setFill(Color.ORANGE);
        moveToA=new MoveTo();
        arcToB=new ArcTo();
        lineToC=new LineTo();
        arcToD=new ArcTo();
        this.getElements().add(moveToA);
        this.getElements().add(arcToB);
        this.getElements().add(lineToC);
        this.getElements().add(arcToD);
        this.getElements().add(new ClosePath());
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

    }

    public void copyValuesFrom(CircleView circleView){

        innerRadius=circleView.innerRadius;
        outerRadius=circleView.outerRadius;
        startAngle=circleView.startAngle;
        endAngle=circleView.endAngle;
        updateView();

        setScaleX(circleView.getScaleX());
        setScaleY(circleView.getScaleX());
        setRotate(circleView.getRotate());
        setTranslateX(circleView.getTranslateX());
        setTranslateY(circleView.getTranslateY());
        setLayoutX(circleView.getLayoutX());
        setLayoutY(circleView.getLayoutY());

        setInnerRadius(circleView.innerRadius);
        setOuterRadius(circleView.outerRadius);
        setStartAngle(circleView.startAngle);
        setEndAngle(circleView.endAngle);
    }
}
