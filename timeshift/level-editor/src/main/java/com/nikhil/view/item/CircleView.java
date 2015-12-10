package com.nikhil.view.item;

import com.nikhil.model.shape.CircleModel;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.CircleViewDelegate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Customizable view for circle that uses Path to display the
 * circle item
 * Created by NikhilVerma on 02/09/15.
 */
public class CircleView extends ShapeView{

    private CircleViewDelegate delegate;

    //=============================================================================================
    //Circle Properties
    //=============================================================================================

    protected DoubleProperty startAngle=new SimpleDoubleProperty();
    protected DoubleProperty endAngle=new SimpleDoubleProperty();
    protected DoubleProperty innerRadius=new SimpleDoubleProperty();
    protected DoubleProperty outerRadius=new SimpleDoubleProperty();


    //=============================================================================================
    //UI Components
    //=============================================================================================
    
    protected MoveTo moveToA;
    protected ArcTo arcToB;
    protected LineTo lineToC;
    protected ArcTo arcToD;

    public CircleView() {
        this(0,1, CircleModel.DEFAULT_STARTING_ANGLE,CircleModel.DEFAULT_ENDING_ANGLE);
    }

    public CircleView(CircleView circleView){
        this(circleView.innerRadius.get(),circleView.outerRadius.get(),circleView.startAngle.get(),circleView.endAngle.get());
    }

    public CircleView(double innerRadius,double outerRadius, double startAngle, double endAngle) {
        this.outerRadius.set(outerRadius);
        this.innerRadius.set(innerRadius);
        this.endAngle.set(endAngle);
        this.startAngle.set(startAngle);
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

    public void setStartAngle(double startAngle) {
        this.startAngle.set(startAngle);
        updateView();
    }


    public void setEndAngle(double endAngle) {
        this.endAngle.set(endAngle);
        updateView();
    }

    public double getInnerRadius() {
        return innerRadius.get();
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius.set(innerRadius);
        updateView();
    }

    public double getOuterRadius() {
        return outerRadius.get();
    }

    public void setOuterRadius(double outerRadius) {
        this.outerRadius.set(outerRadius);
        updateView();
    }

    public double getStartAngle() {
        return startAngle.get();
    }

    public double getEndAngle() {
        return endAngle.get();
    }

    public DoubleProperty startAngleProperty() {
        return startAngle;
    }

    public DoubleProperty endAngleProperty() {
        return endAngle;
    }

    public DoubleProperty innerRadiusProperty() {
        return innerRadius;
    }

    public DoubleProperty outerRadiusProperty() {
        return outerRadius;
    }

    @Override
    public void updateView(){
        //set internal properties
        double startAngleR = Math.toRadians(startAngle.get());
        double endAngleR = Math.toRadians(endAngle.get());
//        double startAngleR = Math.toRadians(rotatedAngle(startAngle.get()));
//        double endAngleR = Math.toRadians(rotatedAngle(endAngle.get()));

        double ax=Math.cos(startAngleR)*innerRadius.get();
        double ay=Math.sin(startAngleR)*innerRadius.get();
        moveToA.setX(ax);
        moveToA.setY(ay);
        UtilPoint rotatedA=rotatedAroundOrigin(ax,ay);
        moveToA.setX(rotatedA.getX());
        moveToA.setY(rotatedA.getY());

        double bx=Math.cos(endAngleR)*innerRadius.get();
        double by=Math.sin(endAngleR)*innerRadius.get();
        arcToB.setRadiusX(innerRadius.get());
        arcToB.setRadiusY(innerRadius.get());
        arcToB.setSweepFlag(true);
        arcToB.setLargeArcFlag(endAngle.get()-startAngle.get()>=180);

        arcToB.setX(bx);
        arcToB.setY(by);
        final UtilPoint rotatedB = rotatedAroundOrigin(bx, by);
        arcToB.setX(rotatedB.getX());
        arcToB.setY(rotatedB.getY());

        double cx=Math.cos(endAngleR)*outerRadius.get();
        double cy=Math.sin(endAngleR)*outerRadius.get();
        lineToC.setX(cx);
        lineToC.setY(cy);
        final UtilPoint rotatedC = rotatedAroundOrigin(cx, cy);
        lineToC.setX(rotatedC.getX());
        lineToC.setY(rotatedC.getY());

        double dx=Math.cos(startAngleR)*outerRadius.get();
        double dy=Math.sin(startAngleR)*outerRadius.get();
        arcToD.setX(dx);
        arcToD.setY(dy);
        final UtilPoint rotatedD = rotatedAroundOrigin(dx, dy);
        arcToD.setX(rotatedD.getX());
        arcToD.setY(rotatedD.getY());
        arcToD.setRadiusX(outerRadius.get());
        arcToD.setRadiusY(outerRadius.get());
        arcToD.setSweepFlag(false);
        arcToD.setLargeArcFlag(endAngle.get()-startAngle.get()>=180);

    }

    public void copyValuesFrom(CircleView circleView){

        this.innerRadius.set(circleView.getInnerRadius());
        this.outerRadius.set(circleView.getOuterRadius());
        this.startAngle.set(circleView.getStartAngle());
        this.endAngle.set(circleView.getEndAngle());


        setScale(circleView.getScale());
        setRotate(circleView.getRotate());
        setOriginRotate(circleView.getOriginRotate());
        setTranslateX(circleView.getTranslateX());
        setTranslateY(circleView.getTranslateY());

        updateView();
//        setInnerRadius(circleView.innerRadius.get());
//        setOuterRadius(circleView.outerRadius.get());
//        setStartAngle(circleView.startAngle.get());
//        setEndAngle(circleView.endAngle.get());
    }

    public CircleViewDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(CircleViewDelegate delegate) {
        this.delegate = delegate;
        setDelegateAsEventHandler();
    }
}
