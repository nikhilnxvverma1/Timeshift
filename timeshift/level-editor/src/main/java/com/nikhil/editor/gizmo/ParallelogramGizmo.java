package com.nikhil.editor.gizmo;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.view.item.ParallelogramView;
import com.nikhil.view.util.ViewUtil;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 04/09/15.
 */
public class ParallelogramGizmo implements EventHandler<MouseEvent>{

    public static final int HANDLE_RADIUS = 3;
    public static final int LOWEST_WIDTH = 20;
    public static final int LOWEST_HEIGHT = 20;
    public static final int SWAY_ANGLE_GAP = 20;

    private ParallelogramView parallelogramView;
    private ParallelogramView outlineParallelogram;
    private Circle lowerWidthHandle;
    private Circle rightHeightHandle;
    private Circle upperWidthHandle;
    private Circle leftHeightHandle;
    private Circle swayHandle;

    public ParallelogramGizmo(Pane pane,ParallelogramView parallelogramView) {
        this.parallelogramView = parallelogramView;
        outlineParallelogram=new ParallelogramView(parallelogramView);
        pane.getChildren().add(outlineParallelogram);
        initializeGraphics(pane);
        updateGraphics();
    }

    private void initializeGraphics(Pane pane){

        lowerWidthHandle=getGenericHandle();
        upperWidthHandle=getGenericHandle();
        rightHeightHandle=getGenericHandle();
        leftHeightHandle=getGenericHandle();
        swayHandle=getGenericHandle();

        //TODO define constants somewhere
        outlineParallelogram.setFill(null);
        outlineParallelogram.setStroke(Color.BLACK);
        outlineParallelogram.getStrokeDashArray().add(7d);
        pane.getChildren().addAll(lowerWidthHandle,upperWidthHandle,leftHeightHandle,rightHeightHandle,swayHandle);

    }

    private Circle getGenericHandle(){
        Circle genericHandle=new Circle();
        genericHandle.setRadius(HANDLE_RADIUS);
        genericHandle.setFill(Color.WHITE);
        genericHandle.setStroke(Color.BLACK);
        genericHandle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);
        genericHandle.setCursor(Cursor.HAND);
        return genericHandle;
    }

    private void updateGraphics(){


        double x=parallelogramView.getTranslationX();
        double y=parallelogramView.getTranslationY();
        outlineParallelogram.setLayoutX(x);
        outlineParallelogram.setLayoutY(y);
        swayHandle.setCenterX(x);
        swayHandle.setCenterY(y);

        Point2D a=parallelogramView.getLowerLeft();
        Point2D b=parallelogramView.getLowerRight();
        Point2D c=parallelogramView.getUpperRight();
        Point2D d=parallelogramView.getUpperLeft();
        Point2D abMid=new Point2D((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);
        Point2D bcMid=new Point2D((b.getX()+c.getX())/2, (b.getY()+c.getY())/2);
        Point2D cdMid=new Point2D((c.getX()+d.getX())/2, (c.getY()+d.getY())/2);
        Point2D daMid=new Point2D((d.getX()+a.getX())/2, (d.getY()+a.getY())/2);

        lowerWidthHandle.setCenterX(abMid.getX()+x);
        lowerWidthHandle.setCenterY(abMid.getY()+y);

        rightHeightHandle.setCenterX(bcMid.getX()+x);
        rightHeightHandle.setCenterY(bcMid.getY()+y);

        upperWidthHandle.setCenterX(cdMid.getX()+x);
        upperWidthHandle.setCenterY(cdMid.getY()+y);

        leftHeightHandle.setCenterX(daMid.getX()+x);
        leftHeightHandle.setCenterY(daMid.getY()+y);

    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==lowerWidthHandle){//AB
            tweakHeight(event);
        }else if(event.getTarget()==leftHeightHandle){//BC
            tweakWidth(event);
        }else if(event.getTarget()==upperWidthHandle){//CD
            tweakHeight(event);
        }else if(event.getTarget()==rightHeightHandle){//DA
            tweakWidth(event);
        }else if(event.getTarget()==swayHandle){//handle at center
            tweakSwayAngle(event);
        }

    }

    private void tweakSwayAngle(MouseEvent event) {
        double boundingHeight=parallelogramView.getBoundingHeight();
        double distanceFromMidX=Math.cos(Math.toRadians(parallelogramView.getSwayAngle()))*boundingHeight/2;
        double abMidX=distanceFromMidX+parallelogramView.getTranslationX();
        double abMidY=-boundingHeight/2+parallelogramView.getTranslationY();
        double x=event.getX();
        double y=event.getY();
        double newAngle= MathUtil.angleOfPoint(abMidX,abMidY,x,y);
        if(newAngle>=0+SWAY_ANGLE_GAP&&newAngle<=180- SWAY_ANGLE_GAP){
            Logger.log("new sway angle ="+newAngle);
            parallelogramView.setSwayAngle(newAngle);
            outlineParallelogram.setSwayAngle(newAngle);
            updateGraphics();
        }
        event.consume();
    }

    private void tweakHeight(MouseEvent event) {
        double centerX=parallelogramView.getTranslationX();
        double centerY=parallelogramView.getTranslationY();
        double x=event.getX();
        double y=event.getY();
        double projectedBoundingHeight=2*Math.abs(centerY-y);//TODO replace for new bounding height formula
        if(projectedBoundingHeight/2>0+ LOWEST_HEIGHT){
            double newHeight= projectedBoundingHeight / Math.sin(Math.toRadians(parallelogramView.getSwayAngle()));
            parallelogramView.setHeight(newHeight);
            outlineParallelogram.setHeight(newHeight);
            updateGraphics();
        }
        event.consume();
    }

    private void tweakWidth(MouseEvent event) {
        double centerX=parallelogramView.getTranslationX();
        double centerY=parallelogramView.getTranslationY();
        double x=event.getX();
        double y=event.getY();
        double halfExtrusion = (parallelogramView.getHeight() / 2) *
                Math.cos(Math.toRadians(parallelogramView.getSwayAngle()));
        double projectedBoundingWidth=2*(Math.abs(centerX-x)+ halfExtrusion);//TODO replace for new bounding width formula
        if(projectedBoundingWidth> LOWEST_WIDTH){
            double newWidth= projectedBoundingWidth-2*halfExtrusion;
            parallelogramView.setWidth(newWidth);
            outlineParallelogram.setWidth(newWidth);
            updateGraphics();
        }
        event.consume();
    }
}
