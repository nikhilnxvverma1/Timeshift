package com.nikhil.view.item;

import com.nikhil.model.shape.TriangleModel;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import com.nikhil.view.item.delegate.TriangleViewDelegate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

/**
 * Customizable triangle view that uses path for rendering.
 * Created by NikhilVerma on 06/09/15.
 */
public class TriangleView extends ShapeView{

    private TriangleViewDelegate delegate;

    private DoubleProperty base=new SimpleDoubleProperty(TriangleModel.DEFAULT_BASE);
    private DoubleProperty height=new SimpleDoubleProperty(TriangleModel.DEFAULT_HEIGHT);


    //=============================================================================================
    //UI components
    //=============================================================================================

    private MoveTo right;
    private LineTo left;
    private LineTo tip;

    public TriangleView(TriangleView triangleView) {
        this(triangleView.base.get(), triangleView.height.get());
    }

    public TriangleView(double base, double height) {
        this.base.set(base);
        this.height.set(height);
        initView();
        updateView();
    }

    private void initView(){
        right=new MoveTo();
        left=new LineTo();
        tip=new LineTo();
        this.getElements().addAll(right,left,tip,new ClosePath());

        this.setFill(Color.ORANGE);
        this.setStroke(null);
    }

    public double getBase() {
        return base.get();
    }

    public DoubleProperty baseProperty() {
        return base;
    }

    public void setBase(double base) {
        this.base.set(base);
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    @Override
    public void updateView(){
        updateSpecialProperties();
    }

    public Point2D getRightPoint(){
        double rx= base.get() /2;
        double ry=0;
        return new Point2D(rx,ry);
    }

    public Point2D getLeftPoint(){
        double lx=-base.get() /2;
        double ly=0;
        return new Point2D(lx,ly);
    }

    public Point2D getTipPoint(){
//        double angle= 180 - (90 + tipAngle / 2);
//        double height= Math.abs((Math.tan(Math.toRadians(angle)) * (base / 2)));
        double tx=0;
        double ty=-height.get();
        return new Point2D(tx,ty);
    }

    public void updateSpecialProperties() {
        Point2D rightPoint=getRightPoint();

        right.setX(rightPoint.getX());
        right.setY(rightPoint.getY());

        Point2D leftPoint=getLeftPoint();
        left.setX(leftPoint.getX());
        left.setY(leftPoint.getY());

        Point2D tipPoint=getTipPoint();
        tip.setX(tipPoint.getX());
        tip.setY(tipPoint.getY());
    }

    public void setDelegate(TriangleViewDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public TriangleViewDelegate getDelegate() {
        return delegate;
    }
}


