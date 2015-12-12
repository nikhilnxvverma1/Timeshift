package com.nikhil.editor.gizmo;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.view.item.CircleView;
import com.nikhil.view.item.delegate.CircleViewDelegate;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Gizmo widget that allows customizing a circle from the front end.
 * A {@link CircleViewDelegate} provides mechanism for handling all the callbacks
 * Created by NikhilVerma on 30/08/15.
 */
public class CircleGizmo extends Gizmo{

    public static final double MAX_ANGLE_DEVIATION_WHILE_TWEAKING_INNER_RADIUS= 90;
    public static final double MAX_ANGLE = 359.99;
    /** Used internally to capture the initial value across press-drag-release events*/
    private static double initialValue;
    private CircleView circleView;
    private CircleView outlineCircle;
    private Circle innerRadiusHandle;
    private Circle outerRadiusHandle;
    private Circle startAngleHandle;
    private Circle endAngleHandle;

    public CircleGizmo(CircleView circleView){
        this.circleView=circleView;
        initView();
        showGizmo(GizmoVisibilityOption.HIDE_ALL);
    }

    private void initView(){
        outlineCircle=new CircleView(circleView);
        outlineCircle.setFill(null);
        outlineCircle.setStroke(OUTLINE_COLOR);
        outlineCircle.getStrokeDashArray().add(OUTLINE_STROKE_DASH);
        outlineCircle.setLayoutX(0);
        outlineCircle.setLayoutY(0);
        outlineCircle.setScale(1);
        outlineCircle.setRotate(0);

        outlineCircle.setStroke(OUTLINE_COLOR);
        outlineCircle.getStrokeDashArray().add(OUTLINE_STROKE_DASH);
        this.getChildren().add(outlineCircle);

        innerRadiusHandle=getGenericHandle();
        this.getChildren().add(innerRadiusHandle);

        outerRadiusHandle=getGenericHandle();
        this.getChildren().add(outerRadiusHandle);

        startAngleHandle=getGenericHandle();
        this.getChildren().add(startAngleHandle);

        endAngleHandle=getGenericHandle();
        this.getChildren().add(endAngleHandle);

        updateView();
    }

    @Override
    public void updateView(){
        outlineCircle.copyValuesFrom(circleView);
        circleView.updateView();
        this.setLayoutX(circleView.getLayoutX());
        this.setLayoutY(circleView.getLayoutY());
        updateInnerRadiusHandle();
        updateOuterRadiusHandle();
        updateStartAngleHandle();
        updateEndAngleHandle();
    }

    @Override
    public Node getOutline() {
        return outlineCircle;
    }

    private void updateEndAngleHandle() {
        double handleRadius=endAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double totalRadius=handleRadius+circleView.getOuterRadius();
        double angle=circleView.getOriginRotate()+circleView.getEndAngle()-handleReductionAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius;//+circleView.getLayoutX();
        double y=Math.sin(angle)*totalRadius;//+circleView.getLayoutY();
//        Logger.log("x,y="+ x+","+y);
        endAngleHandle.setCenterX(x);
        endAngleHandle.setCenterY(y);
    }

    private void updateStartAngleHandle() {
        double handleRadius=startAngleHandle.getRadius();
        double handleElevationAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double totalRadius=handleRadius+circleView.getOuterRadius();
        double angle=circleView.getOriginRotate()+circleView.getStartAngle()+handleElevationAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius;//+circleView.getLayoutX();
        double y=Math.sin(angle)*totalRadius;//+circleView.getLayoutY();
        startAngleHandle.setCenterX(x);
        startAngleHandle.setCenterY(y);
    }

    private void updateInnerRadiusHandle() {
        double averageAngle=circleView.getOriginRotate()+(circleView.getStartAngle()+circleView.getEndAngle())/2;
        averageAngle=Math.toRadians(averageAngle);

        double ix=Math.cos(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutX();
        double iy=Math.sin(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutY();
        innerRadiusHandle.setCenterX(ix);
        innerRadiusHandle.setCenterY(iy);
    }

    private void updateOuterRadiusHandle() {
        double averageAngle=circleView.getOriginRotate()+(circleView.getStartAngle()+circleView.getEndAngle())/2;
        averageAngle=Math.toRadians(averageAngle);
        double ox=Math.cos(averageAngle)*circleView.getOuterRadius();//+circleView.getLayoutX();
        double oy=Math.sin(averageAngle)*circleView.getOuterRadius();//+circleView.getLayoutY();
        outerRadiusHandle.setCenterX(ox);
        outerRadiusHandle.setCenterY(oy);
    }

    public CircleView getCircleView() {
        return circleView;
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==innerRadiusHandle){
            tweakInnerRadius(event);
        }else if(event.getTarget()==outerRadiusHandle){
            tweakOuterRadius(event);
        }else if(event.getTarget()==startAngleHandle){
            tweakStartAngle(event);
        }else if(event.getTarget()==endAngleHandle){
            tweakEndAngle(event);
        }
    }

    private void tweakEndAngle(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=circleView.getEndAngle();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            circleView.getDelegate().finishedTweakingEndAngle(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){

            //discount the small angle shift created for the handle
            double handleRadius=endAngleHandle.getRadius();
            double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));

            double centerX=0;//circleView.getLayoutX();
            double centerY=0;//circleView.getLayoutY();
            double angleX=event.getX();//+endAngleHandle.getTranslateX();
            double angleY=event.getY();//+endAngleHandle.getTranslateY();
            double absoluteEndAngle=circleView.getEndAngle()+circleView.getOriginRotate();
            double angleOfPoint=MathUtil.angleOfPoint(centerX, centerY, angleX, angleY);
            double deviation= MathUtil.getAngleDifference(angleOfPoint, absoluteEndAngle);
            double newAngle = circleView.getEndAngle() + deviation + handleReductionAngle;
            final double oldEndAngle = circleView.getEndAngle();
            if(newAngle < MAX_ANGLE) {
                if((newAngle >= 0) && (newAngle > circleView.getStartAngle())){
                    circleView.setEndAngle(newAngle);
                    outlineCircle.setEndAngle(newAngle);
                    updateEndAngleHandle();
                    updateOuterRadiusHandle();
                    updateInnerRadiusHandle();
                    circleView.getDelegate().tweakingEndAngle(oldEndAngle,newAngle);
                }
            }else{
                circleView.setEndAngle(MAX_ANGLE);
                outlineCircle.setEndAngle(MAX_ANGLE);
                updateEndAngleHandle();
                updateOuterRadiusHandle();
                updateInnerRadiusHandle();
                circleView.getDelegate().tweakingEndAngle(oldEndAngle,MAX_ANGLE);
            }
            circleView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        event.consume();
    }

    private void tweakStartAngle(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=circleView.getStartAngle();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            circleView.getDelegate().finishedTweakingStartAngle(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            //discount the small angle shift created for the handle
            double handleRadius=startAngleHandle.getRadius();
            double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
            double centerX=0;//circleView.getLayoutX();
            double centerY=0;//circleView.getLayoutY();
            double angleX=event.getX();//+startAngleHandle.getTranslateX();
            double angleY=event.getY();//+startAngleHandle.getTranslateY();
            double absoluteStartAngle=circleView.getStartAngle()+circleView.getOriginRotate();
            double angleOfPoint=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
            double deviation= MathUtil.getAngleDifference(angleOfPoint, absoluteStartAngle);
            double newAngle = circleView.getStartAngle() + deviation + handleReductionAngle;
            final double oldStartAngle = circleView.getStartAngle();
            if(newAngle >=0) {
                if((newAngle <=MAX_ANGLE) && (newAngle < circleView.getEndAngle())){
                    circleView.setStartAngle(newAngle);
                    outlineCircle.setStartAngle(newAngle);
                    updateStartAngleHandle();
                    updateOuterRadiusHandle();
                    updateInnerRadiusHandle();
                    circleView.getDelegate().tweakingStartAngle(oldStartAngle,newAngle);
                }
            }else{
                circleView.setStartAngle(0);
                outlineCircle.setStartAngle(0);
                updateStartAngleHandle();
                updateOuterRadiusHandle();
                updateInnerRadiusHandle();
                circleView.getDelegate().tweakingStartAngle(oldStartAngle, newAngle);
            }
            circleView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        event.consume();
    }

    private void tweakInnerRadius(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=circleView.getInnerRadius();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            circleView.getDelegate().finishedTweakingInnerRadius(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){

            double centerX=0;//circleView.getLayoutX();
            double centerY=0;//circleView.getLayoutY();
            double innerX=event.getX();//+innerRadiusHandle.getTranslateX();
            double innerY=event.getY();//+innerRadiusHandle.getTranslateY();

            //if the point of the event is anywhere greater than whats allowed ,reject this tweak
            double angleOfHandle=MathUtil.angleOfPoint(0,0,innerRadiusHandle.getCenterX(),innerRadiusHandle.getCenterY());
            angleOfHandle= circleView.getOriginRotate()+(circleView.getStartAngle()+circleView.getEndAngle())/2;
            double angleOfEvent=MathUtil.angleOfPoint(0,0,innerX,innerY);
            //if its a NaN revise it
            if(Double.isNaN(angleOfEvent)){
                angleOfEvent=0;
            }
            //we never supply NaN to this method
            final double angleDifference = MathUtil.abs(MathUtil.getAngleDifference(angleOfEvent, angleOfHandle));

            //keep in mind that the angle difference can be a NaN if all arguments to above methods were 0
            double newInnerRadius;
            if(Double.isNaN(angleDifference)||angleDifference<MAX_ANGLE_DEVIATION_WHILE_TWEAKING_INNER_RADIUS){
                newInnerRadius = MathUtil.distance(centerX, centerY, innerX, innerY);
            }else{
                newInnerRadius=0;
            }
            final double oldInnerRadius = circleView.getInnerRadius();
            if(newInnerRadius<circleView.getOuterRadius()){
                circleView.setInnerRadius(newInnerRadius);
                outlineCircle.setInnerRadius(newInnerRadius);
                updateInnerRadiusHandle();
                circleView.getDelegate().tweakingInnerRadius(oldInnerRadius,newInnerRadius);
            }
            circleView.getDelegate().propertyCurrentlyGettingTweaked();
        }

        event.consume();
    }

    private void tweakOuterRadius(MouseEvent event){

        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=circleView.getOuterRadius();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            circleView.getDelegate().finishedTweakingOuterRadius(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            double centerX=0;//this.getLayoutX();
            double centerY=0;//this.getLayoutY();
            double outerX=event.getX()+outerRadiusHandle.getTranslateX();
            double outerY=event.getY()+outerRadiusHandle.getTranslateY();
            double newOuterRadius= MathUtil.distance(centerX,centerY,outerX,outerY);
            double oldOuterRadius=circleView.getOuterRadius();
            if(newOuterRadius>circleView.getInnerRadius()){
                circleView.setOuterRadius(newOuterRadius);
                outlineCircle.setOuterRadius(newOuterRadius);
                updateOuterRadiusHandle();
                //outer radius affects angle handles
                updateStartAngleHandle();
                updateEndAngleHandle();
                circleView.getDelegate().tweakingOuterRadius(oldOuterRadius,newOuterRadius);
            }
            circleView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        event.consume();
    }
}
