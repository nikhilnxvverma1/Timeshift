package com.nikhil.editor.gizmo;

import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.view.item.CircleView;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Gizmo widget that allows customizing a circle from the front end.
 * A CircleGizmoDelegate provides mechanism for handling all the callbacks
 * Created by NikhilVerma on 30/08/15.
 */
public class CircleGizmo extends Gizmo{

    public static final int LARGE_ANGLE_CHANGE = 300;
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
        outlineCircle.setLayoutX(0);
        outlineCircle.setLayoutY(0);

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
        double angle=circleView.getEndingAngle()-handleReductionAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius;//+circleView.getLayoutX();
        double y=Math.sin(angle)*totalRadius;//+circleView.getLayoutY();
        endAngleHandle.setCenterX(x);
        endAngleHandle.setCenterY(y);
    }

    private void updateStartAngleHandle() {
        double handleRadius=startAngleHandle.getRadius();
        double handleElevationAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double totalRadius=handleRadius+circleView.getOuterRadius();
        double angle=circleView.getStartingAngle()+handleElevationAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius;//+circleView.getLayoutX();
        double y=Math.sin(angle)*totalRadius;//+circleView.getLayoutY();
        startAngleHandle.setCenterX(x);
        startAngleHandle.setCenterY(y);
    }

    private void updateInnerRadiusHandle() {
        double averageAngle=(circleView.getStartingAngle()+circleView.getEndingAngle())/2;
        averageAngle=Math.toRadians(averageAngle);

        double ix=Math.cos(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutX();
        double iy=Math.sin(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutY();
        innerRadiusHandle.setCenterX(ix);
        innerRadiusHandle.setCenterY(iy);
    }

    private void updateOuterRadiusHandle() {
        double averageAngle=(circleView.getStartingAngle()+circleView.getEndingAngle())/2;
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
        double handleRadius=endAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double centerX=0;//circleView.getLayoutX();
        double centerY=0;//circleView.getLayoutY();
        double angleX=event.getX()+endAngleHandle.getTranslateX();
        double angleY=event.getY()+endAngleHandle.getTranslateY();
        double angle=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
        angle+=handleReductionAngle;//discount the small angle shift created for the handle
        double change=circleView.getEndingAngle()-angle;
        //large sudden changes to angle are not allowed(which may arise if the user rotates one full circle
        // and goes over)
        if(angle>circleView.getStartingAngle()){
            System.out.println("end angle to setting to : " + angle);
            if(angle<=359.99){
                circleView.setEndAngle(angle);
                outlineCircle.setEndAngle(angle);
            }else{
                circleView.setEndAngle(359.99);
                outlineCircle.setEndAngle(359.99);
            }
            updateEndAngleHandle();
            updateOuterRadiusHandle();
            updateInnerRadiusHandle();
            circleView.getDelegate().propertyCurrentlyGettingTweaked();
        }else{
            Logger.log("rejecting angle of "+angle);
        }
        event.consume();
    }

    private void tweakStartAngle(MouseEvent event) {
        double handleRadius=startAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double centerX=0;//circleView.getLayoutX();
        double centerY=0;//circleView.getLayoutY();
        double angleX=event.getX()+startAngleHandle.getTranslateX();
        double angleY=event.getY()+startAngleHandle.getTranslateY();
        double angle=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
        angle+=handleReductionAngle;//discount the small angle shift created for the handle
        System.out.println("start angle to possibly set : "+angle);
        if(angle<circleView.getEndingAngle()){
            circleView.setStartAngle(angle);
            outlineCircle.setStartAngle(angle);
            updateStartAngleHandle();
            updateOuterRadiusHandle();
            updateInnerRadiusHandle();
        }
        circleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }

    private void tweakInnerRadius(MouseEvent event) {
        double centerX=0;//circleView.getLayoutX();
        double centerY=0;//circleView.getLayoutY();
        double innerX=event.getX()+innerRadiusHandle.getTranslateX();
        double innerY=event.getY()+innerRadiusHandle.getTranslateY();
        double newInnerRadius= MathUtil.distance(centerX,centerY,innerX,innerY);
        if(newInnerRadius<circleView.getOuterRadius()){
            circleView.setInnerRadius(newInnerRadius);
            outlineCircle.setInnerRadius(newInnerRadius);
            updateInnerRadiusHandle();
//            innerRadiusHandle.setCenterX(innerX);
//            innerRadiusHandle.setCenterY(innerY);

        }
        circleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }

    private void tweakOuterRadius(MouseEvent event){

        double centerX=0;//this.getLayoutX();
        double centerY=0;//this.getLayoutY();
        double outerX=event.getX()+outerRadiusHandle.getTranslateX();
        double outerY=event.getY()+outerRadiusHandle.getTranslateY();
        double newOuterRadius= MathUtil.distance(centerX,centerY,outerX,outerY);
        if(newOuterRadius>circleView.getInnerRadius()){
            circleView.setOuterRadius(newOuterRadius);
            outlineCircle.setOuterRadius(newOuterRadius);
            updateOuterRadiusHandle();
            //outer radius affects angle handles
            updateStartAngleHandle();
            updateEndAngleHandle();
//            outerRadiusHandle.setCenterX(outerX);
//            outerRadiusHandle.setCenterY(outerY);

        }
        circleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }
}
