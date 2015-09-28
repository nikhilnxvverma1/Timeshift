package com.nikhil.view.zoom;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * Zoomabale scroll pane allows holding alt and zooming in and out using
 * scroll events.Event filter for these scroll events should be added by the user.
 */
public class ZoomableScrollPane extends ScrollPane implements EventHandler<ScrollEvent>{

    public static final double DEFAULT_MIN_ZOOM_LIMIT=0.3;
    public static final double DEFAULT_MAX_ZOOM_LIMIT=6.0;

    private Group zoomGroup;
    private Scale scaleTransform;
    private Pane content;
    private double minZoomLimit=DEFAULT_MIN_ZOOM_LIMIT;
    private double maxZoomLimit=DEFAULT_MAX_ZOOM_LIMIT;

    public ZoomableScrollPane() {
        this(new Pane());
    }

    public ZoomableScrollPane(Pane content){
        //TODO set style
        this.content = content;
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(content);
        setContent(contentGroup);
        scaleTransform = new Scale(1 ,1, 0, 0);
        zoomGroup.getTransforms().add(scaleTransform);

    }

    //TODO refactor by breaking all zooming code into several usable utility methods

    @Override
    public void handle(ScrollEvent event) {
        if(event.isAltDown()){
            //On scrolling scale up and down the workspace pane
            //and every child element will automatically take care of itself
            double deltaY = event.getDeltaY();
            //convert from delta to actual percentage(copied,ignore)
//                    double newZoomPercentage=currentZoom+(MathUtil.abs(deltaY)/MAX_SCROLL_DELTA)*100;(copied,ignore)
            double x = scaleTransform.getX();
            double y = scaleTransform.getY();
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();
            Point2D local=this.sceneToLocal(sceneX, sceneY);
            Point2D contentLocal=content.parentToLocal(local.getX(),local.getY());

            double newZoomX,newZoomY;
            if(deltaY>=0){
                newZoomX = x + 0.1;
                newZoomY = y + 0.1;
            }else{
                newZoomX = x - 0.1;
                newZoomY = y - 0.1;
            }
            //ideally both new zoom x,and y should be the same
            if(newZoomX>=minZoomLimit&&newZoomX<=maxZoomLimit){
                this.scaleTransform.setX(newZoomX);
            }
            if (newZoomY>=minZoomLimit&&newZoomY<=maxZoomLimit) {
                this.scaleTransform.setY(newZoomY);
            }

            double ww=content.getWidth();
            double wx=contentLocal.getX();
            double hx=wx/ww;

            double wh=content.getHeight();
            double wy=contentLocal.getY();
            double hy=wy/wh;

            hx=wx/ww;
            hy=wy/wh;

            //zoom relative to the point
            this.setHvalue(hx);
            this.setVvalue(hy);

            event.consume();
        }
    }

    public double getMinZoomLimit() {
        return minZoomLimit;
    }

    public void setMinZoomLimit(double minZoomLimit) {
        this.minZoomLimit = minZoomLimit;
    }

    public double getMaxZoomLimit() {
        return maxZoomLimit;
    }

    public void setMaxZoomLimit(double maxZoomLimit) {
        this.maxZoomLimit = maxZoomLimit;
    }

    public double getCurrentZoomX(){
        return scaleTransform.getX();
    }

    public double getCurrentZoomY(){
        return scaleTransform.getY();
    }
}

