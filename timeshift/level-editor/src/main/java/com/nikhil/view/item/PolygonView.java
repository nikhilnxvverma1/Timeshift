package com.nikhil.view.item;

import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.PolygonViewDelegate;
//import javafx.geometry.Point2D;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.List;

/**
 * Created by NikhilVerma on 06/09/15.
 */
public class PolygonView extends Path{

    private PolygonViewDelegate delegate;

    //=============================================================================================
    //Polygon Properties
    //=============================================================================================

    private double scale;
    private double rotation;
    private double translationX;
    private double translationY;
    private double anchorPointX;
    private double anchorPointY;

    private List<UtilPoint> polygonPoints;

    public PolygonView(PolygonView polygonView) {
        this(polygonView.polygonPoints,polygonView.translationX,polygonView.translationY,polygonView.rotation,polygonView.scale);
    }

    public PolygonView(List<UtilPoint> polygonPoints,double translationX,double translationY,double rotation,double scale) {
        this.polygonPoints =polygonPoints;
        this.translationX=translationX;
        this.translationY=translationY;
        this.rotation=rotation;
        this.scale=scale;
        initializeView();
        updateTransform();
    }

    private void initializeView(){
        int index=0;
        for(UtilPoint point: polygonPoints){
            if(index==0){
                MoveTo firstPolygonPoint=new MoveTo();
                this.getElements().add(firstPolygonPoint);
                firstPolygonPoint.setX(point.getX());
                firstPolygonPoint.setY(point.getY());
            }else{
                LineTo polylineTo=new LineTo();
                this.getElements().add(polylineTo);
                polylineTo.setX(point.getX());
                polylineTo.setY(point.getY());
            }
            index++;
        }
        this.getElements().add(new ClosePath());
        this.setFill(Color.ORANGE);
        this.setStroke(null);
    }

    public void updatePoint(int index,UtilPoint newLocation){
        if(index==0){
            MoveTo firstPolygonPoint=(MoveTo)this.getElements().get(index);
            firstPolygonPoint.setX(newLocation.getX());
            firstPolygonPoint.setY(newLocation.getY());

        }else{
            LineTo polylineTo=(LineTo)this.getElements().get(index);
            polylineTo.setX(newLocation.getX());
            polylineTo.setY(newLocation.getY());
        }
        UtilPoint polygonPoint = polygonPoints.get(index);
        polygonPoint.set(newLocation.getX(), newLocation.getY());

    }

    public void updateView(){
        updateTransform();
        updatePoints();

    }

    public void updatePoints() {
        int index=0;
        for(UtilPoint utilPoint : polygonPoints){
            updatePoint(index, utilPoint);
            index++;
        }
    }

    public void updateTransform(){
        this.setLayoutX(translationX);
        this.setLayoutY(translationY);
        this.setRotate(rotation);
        this.setScaleX(scale);
        this.setScaleY(scale);
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

    public List<UtilPoint> getPolygonPoints() {
        return polygonPoints;
    }

    public void setPolygonPoints(List<UtilPoint> polygonPoints) {
        this.polygonPoints = polygonPoints;
        updatePoints();
    }

    public PolygonViewDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(PolygonViewDelegate delegate) {
        this.delegate = delegate;
        if(delegate!=null){
            this.setOnMousePressed((e) -> {
                delegate.mousePressed(e);
            });
            this.setOnMouseDragged((e)->{
                delegate.mouseDragged(e);
            });
            this.setOnMouseReleased((e)->{
                delegate.mouseReleased(e);
            });
        }else{
            this.setOnMousePressed(null);
            this.setOnMouseDragged(null);
            this.setOnMouseReleased(null);
        }
    }
}
