package com.nikhil.view.item;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Created by NikhilVerma on 06/09/15.
 */
public class IsoscelesTriangleView extends Path{

    //=============================================================================================
    //Isosceles Triangle properties
    //=============================================================================================

    private double scale;
    private double rotation;
    private double translationX;
    private double translationY;
    private double anchorPointX;
    private double anchorPointY;

    private double base;
//    private double tipAngle;
    private double height;

    //=============================================================================================
    //UI components
    //=============================================================================================

    private MoveTo right;
    private LineTo left;
    private LineTo tip;

    public IsoscelesTriangleView(IsoscelesTriangleView isoscelesTriangleView) {
        this(isoscelesTriangleView.base,isoscelesTriangleView.height,
                isoscelesTriangleView.translationX,isoscelesTriangleView.translationY,
                isoscelesTriangleView.rotation,isoscelesTriangleView.scale);
    }

    public IsoscelesTriangleView(double base, double height,double translationX,double translationY,double rotation,double scale) {
        this.base = base;
        this.height = height;
        this.translationX=translationX;
        this.translationY=translationY;
        this.rotation=rotation;
        this.scale=scale;
        initializeView();
        updateView();
    }

    public void initializeView(){
        right=new MoveTo();
        left=new LineTo();
        tip=new LineTo();
        this.getElements().addAll(right,left,tip,new ClosePath());

        this.setFill(Color.ORANGE);
        this.setStroke(null);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        updateTransform();
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        updateTransform();
    }

    public double getTranslationX() {
        return translationX;
    }

    public void setTranslationX(double translationX) {
        this.translationX = translationX;
        updateTransform();
    }

    public double getTranslationY() {
        return translationY;
    }

    public void setTranslationY(double translationY) {
        this.translationY = translationY;
        updateTransform();
    }

    public double getAnchorPointX() {
        return anchorPointX;
    }

    public void setAnchorPointX(double anchorPointX) {
        this.anchorPointX = anchorPointX;
        updateTransform();
    }

    public double getAnchorPointY() {
        return anchorPointY;
    }

    public void setAnchorPointY(double anchorPointY) {
        this.anchorPointY = anchorPointY;
        updateTransform();
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
        updateSpecialProperties();
    }

//    public double getTipAngle() {
//        return tipAngle;
//    }
//
//    public void setTipAngle(double tipAngle) {
//        this.tipAngle = tipAngle;
//        updateSpecialProperties();
//    }


    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        updateSpecialProperties();
    }

    public void updateView(){
        updateSpecialProperties();
        updateTransform();
    }

    public Point2D getRightPoint(){
        double rx= base /2;
        double ry=0;
        return new Point2D(rx,ry);
    }

    public Point2D getLeftPoint(){
        double lx=-base /2;
        double ly=0;
        return new Point2D(lx,ly);
    }

    public Point2D getTipPoint(){
//        double angle= 180 - (90 + tipAngle / 2);
//        double height= Math.abs((Math.tan(Math.toRadians(angle)) * (base / 2)));
        double tx=0;
        double ty=-height;
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

    public void updateTransform(){
        this.setLayoutX(translationX);
        this.setLayoutY(translationY);
        this.setScaleX(scale);
        this.setScaleY(scale);
        this.setRotate(rotation);
    }
}
