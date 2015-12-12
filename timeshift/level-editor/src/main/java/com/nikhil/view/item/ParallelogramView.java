package com.nikhil.view.item;

import com.nikhil.model.shape.ParallelogramModel;
import com.nikhil.view.item.delegate.ParallelogramViewDelegate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

/**
 * 
 * Customizable view for parallelogram that uses Path to display the
 * parallelogram item
 * Created by NikhilVerma on 04/09/15.
 */
public class ParallelogramView extends ShapeView{

    private ParallelogramViewDelegate delegate;

    private DoubleProperty width=new SimpleDoubleProperty(ParallelogramModel.DEFAULT_WIDTH);
    private DoubleProperty height=new SimpleDoubleProperty(ParallelogramModel.DEFAULT_HEIGHT);
    private DoubleProperty swayAngle=new SimpleDoubleProperty(ParallelogramModel.DEFAULT_SWAY_ANGLE);

    //=============================================================================================
    //UI components
    //=============================================================================================

    protected MoveTo moveToA;
    protected LineTo lineToB;
    protected LineTo lineToC;
    protected LineTo lineToD;


    public ParallelogramView(double width, double height, double swayAngle) {
        this.swayAngle.set(swayAngle);
        this.height.set(height);
        this.width.set(width);
        initView();
        updateView();
    }

    public ParallelogramView(ParallelogramView parallelogramView) {
        this(parallelogramView.width.get(),parallelogramView.height.get(),parallelogramView.swayAngle.get());
    }
    
    public double getWidth() {
        return width.get();
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double width) {
        this.width.set(width);
        updateView();
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
        updateView();
    }

    public double getSwayAngle() {
        return swayAngle.get();
    }

    public DoubleProperty swayAngleProperty() {
        return swayAngle;
    }

    public void setSwayAngle(double swayAngle) {
        this.swayAngle.set(swayAngle);
        updateView();
    }

    private void initView(){
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
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle.get()))*(height.get()/2);
        double ax= width.get() / 2 + distanceFromMidX;
        double ay=boundingHeight/2;
        return new Point2D(ax,ay);
    }

    /**
     * neverminding the y axis going downwards
     * @return the lower right point on the parallelogram
     */
    public Point2D getLowerLeft(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle.get()))*(height.get()/2);
        double bx= -width.get() / 2 + distanceFromMidX;
        double by=boundingHeight/2;
        return new Point2D(bx,by);
    }


    /**
     * neverminding the y axis going downwards
     * @return the upper left point on the parallelogram
     */
    public Point2D getUpperLeft(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle.get()))*(height.get()/2);
        double cx= -width.get() / 2 - distanceFromMidX;
        double cy=-boundingHeight/2;
        return new Point2D(cx,cy);
    }


    /**
     * neverminding the y axis going downwards
     * @return the upper right point on the parallelogram
     */
    public Point2D getUpperRight(){
        double boundingHeight=getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(swayAngle.get()))*(height.get()/2);
        double dx= width.get() / 2 - distanceFromMidX;
        double dy=-boundingHeight/2;
        return new Point2D(dx,dy);
    }

    @Override
    public ParallelogramViewDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ParallelogramViewDelegate delegate) {
        this.delegate = delegate;
        setDelegateAsEventHandler();
    }

    @Override
    public void updateView(){

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

    }

    /**
     * @return the absolute bounding height of the parallelogram
     */
    public double getBoundingHeight(){
        return Math.abs(Math.sin(Math.toRadians(swayAngle.get()))*height.get());
    }

    /**
     * @return the absolute bounding width of the parallelogram
     */

    public double getBoundingWidth(){
        return width.get()+Math.abs(2*Math.cos(Math.toRadians(swayAngle.get()))*height.get());
    }


    /**
     * Copies all properties from the specified view <b>EXCEPT</b> layout x,y properties
     * @param parallelogramView the view to copy values from
     */
    public void copyValuesFrom(ParallelogramView parallelogramView){
        
        this.setWidth(parallelogramView.getWidth());
        this.setHeight(parallelogramView.getHeight());
        this.setSwayAngle(parallelogramView.getSwayAngle());

        setScale(parallelogramView.getScale());
        setRotate(parallelogramView.getRotate());
        setOriginRotate(parallelogramView.getOriginRotate());
        setTranslateX(parallelogramView.getTranslateX());
        setTranslateY(parallelogramView.getTranslateY());

        updateView();
    }
}
