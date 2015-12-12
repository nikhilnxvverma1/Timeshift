package com.nikhil.editor.gizmo;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.ParallelogramView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 04/09/15.
 */
public class ParallelogramGizmo extends Gizmo{

    /** Used internally to capture the initial value across press-drag-release events*/
    private static double initialValue;
    
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

    public ParallelogramGizmo(ParallelogramView parallelogramView) {
        this.parallelogramView = parallelogramView;
        initView();
        showGizmo(GizmoVisibilityOption.HIDE_ALL);
    }

    private void initView(){

        outlineParallelogram=new ParallelogramView(parallelogramView);
        outlineParallelogram.setLayoutX(0);
        outlineParallelogram.setLayoutY(0);
        lowerWidthHandle=getGenericHandle();
        upperWidthHandle=getGenericHandle();
        rightHeightHandle=getGenericHandle();
        leftHeightHandle=getGenericHandle();
        swayHandle=getGenericHandle();
        
        outlineParallelogram.setFill(null);
        outlineParallelogram.setStroke(OUTLINE_COLOR);
        outlineParallelogram.getStrokeDashArray().add(OUTLINE_STROKE_DASH);
        this.getChildren().addAll(outlineParallelogram,lowerWidthHandle,upperWidthHandle,leftHeightHandle,rightHeightHandle,swayHandle);
        updateView();
    }

    @Override
    public ParallelogramView getOutline() {
        return outlineParallelogram;
    }

    @Override
    public void updateView() {
        this.setLayoutX(parallelogramView.getLayoutX());
        this.setLayoutY(parallelogramView.getLayoutY());
        double x=0;//parallelogramView.getTranslationX();
        double y=0;//parallelogramView.getTranslationY();
        outlineParallelogram.copyValuesFrom(parallelogramView);
        swayHandle.setCenterX(x);
        swayHandle.setCenterY(y);

        UtilPoint a=parallelogramView.getLowerLeft();
        UtilPoint b=parallelogramView.getLowerRight();
        UtilPoint c=parallelogramView.getUpperRight();
        UtilPoint d=parallelogramView.getUpperLeft();
        UtilPoint abMid=new UtilPoint((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);
        UtilPoint bcMid=new UtilPoint((b.getX()+c.getX())/2, (b.getY()+c.getY())/2);
        UtilPoint cdMid=new UtilPoint((c.getX()+d.getX())/2, (c.getY()+d.getY())/2);
        UtilPoint daMid=new UtilPoint((d.getX()+a.getX())/2, (d.getY()+a.getY())/2);

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
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=parallelogramView.getSwayAngle();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            parallelogramView.getDelegate().finishedTweakingSwayAngle(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){

            double boundingHeight=parallelogramView.getBoundingHeight();
            double distanceFromMidX=Math.cos(Math.toRadians(parallelogramView.getSwayAngle()))*boundingHeight/2;
            double abMidX=distanceFromMidX;//+parallelogramView.getTranslationX();
            double abMidY=-boundingHeight/2;//+parallelogramView.getTranslationY();
            double x=event.getX();
            double y=event.getY();
            double newSwayAngle= MathUtil.angleOfPoint(abMidX,abMidY,x,y);
            if(newSwayAngle>=0+SWAY_ANGLE_GAP&&newSwayAngle<=180- SWAY_ANGLE_GAP){
                double oldSwayAngle=parallelogramView.getSwayAngle();
                parallelogramView.setSwayAngle(newSwayAngle);
                updateView();
                parallelogramView.getDelegate().tweakingSwayAngle(oldSwayAngle,newSwayAngle);
            }
            parallelogramView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        
        event.consume();
    }

    private void tweakHeight(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=parallelogramView.getHeight();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            parallelogramView.getDelegate().finishedTweakingHeight(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            double centerX=0;//parallelogramView.getTranslationX();
            double centerY=0;//parallelogramView.getTranslationY();
            double x=event.getX();
            double y=event.getY();
            double projectedBoundingHeight=2*Math.abs(centerY-y);//TODO replace for new bounding height formula
            if(projectedBoundingHeight/2>0+ LOWEST_HEIGHT){
                double oldHeight=parallelogramView.getHeight();
                double newHeight= projectedBoundingHeight / Math.sin(Math.toRadians(parallelogramView.getSwayAngle()));
                parallelogramView.setHeight(newHeight);
                outlineParallelogram.setHeight(newHeight);
                updateView();
                parallelogramView.getDelegate().tweakingHeight(oldHeight,newHeight);
            }
            parallelogramView.getDelegate().propertyCurrentlyGettingTweaked();
        }

        event.consume();
    }

    private void tweakWidth(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=parallelogramView.getWidth();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            parallelogramView.getDelegate().finishedTweakingWidth(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            double centerX=0;//parallelogramView.getTranslationX();
            double centerY=0;//parallelogramView.getTranslationY();
            double x=event.getX();
            double y=event.getY();
            double halfExtrusion = (parallelogramView.getHeight() / 2) *
                    Math.cos(Math.toRadians(parallelogramView.getSwayAngle()));
            double projectedBoundingWidth=2*(Math.abs(centerX-x)+ halfExtrusion);//TODO replace for new bounding width formula
            if(projectedBoundingWidth> LOWEST_WIDTH){
                double oldWidth=parallelogramView.getWidth();
                double newWidth= projectedBoundingWidth-2*halfExtrusion;
                parallelogramView.setWidth(newWidth);
                updateView();
                parallelogramView.getDelegate().tweakingWidth(oldWidth,newWidth);
            }
            parallelogramView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        event.consume();
    }
}
