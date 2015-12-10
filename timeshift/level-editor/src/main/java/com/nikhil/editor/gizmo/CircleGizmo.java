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
    public static final double MAX_ANGLE = 359.99;
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
        double angle=circleView.getRotate()+circleView.getEndAngle()-handleReductionAngle;
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
        double angle=circleView.getRotate()+circleView.getStartAngle()+handleElevationAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius;//+circleView.getLayoutX();
        double y=Math.sin(angle)*totalRadius;//+circleView.getLayoutY();
        startAngleHandle.setCenterX(x);
        startAngleHandle.setCenterY(y);
    }

    private void updateInnerRadiusHandle() {
        double averageAngle=circleView.getRotate()+(circleView.getStartAngle()+circleView.getEndAngle())/2;
        averageAngle=Math.toRadians(averageAngle);

        double ix=Math.cos(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutX();
        double iy=Math.sin(averageAngle)*circleView.getInnerRadius();//+circleView.getLayoutY();
        innerRadiusHandle.setCenterX(ix);
        innerRadiusHandle.setCenterY(iy);
    }

    private void updateOuterRadiusHandle() {
        double averageAngle=circleView.getRotate()+(circleView.getStartAngle()+circleView.getEndAngle())/2;
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
        //discount the small angle shift created for the handle
        double handleRadius=endAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));

        double centerX=0;//circleView.getLayoutX();
        double centerY=0;//circleView.getLayoutY();
        double angleX=event.getX();//+endAngleHandle.getTranslateX();
        double angleY=event.getY();//+endAngleHandle.getTranslateY();
        double absoluteEndAngle=circleView.getEndAngle()+circleView.getRotate();
        double angleOfPoint=MathUtil.angleOfPoint(centerX, centerY, angleX, angleY);
        double deviation= MathUtil.getAngleDifference(angleOfPoint, absoluteEndAngle);
        double newAngle = circleView.getEndAngle() + deviation + handleReductionAngle;
//        Logger.log(deviation+" : deviation, new angle"+newAngle);
        Logger.log(angleOfPoint+" : angleOfPoint,"+absoluteEndAngle+" absoluteEndAngle");
        if(newAngle < MAX_ANGLE) {
            if((newAngle >= 0) && (newAngle > circleView.getStartAngle())){
                circleView.setEndAngle(newAngle);
                outlineCircle.setEndAngle(newAngle);
                updateEndAngleHandle();
                updateOuterRadiusHandle();
                updateInnerRadiusHandle();
            }
        }else{
            circleView.setEndAngle(MAX_ANGLE);
            outlineCircle.setEndAngle(MAX_ANGLE);
            updateEndAngleHandle();
            updateOuterRadiusHandle();
            updateInnerRadiusHandle();
        }
        circleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }

    private void tweakStartAngle(MouseEvent event) {
        //discount the small angle shift created for the handle
        double handleRadius=startAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double centerX=0;//circleView.getLayoutX();
        double centerY=0;//circleView.getLayoutY();
        double angleX=event.getX();//+startAngleHandle.getTranslateX();
        double angleY=event.getY();//+startAngleHandle.getTranslateY();
        double absoluteStartAngle=circleView.getStartAngle()+circleView.getRotate();
        double angleOfPoint=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
        double deviation= MathUtil.getAngleDifference(angleOfPoint, absoluteStartAngle);
        double newAngle = circleView.getStartAngle() + deviation + handleReductionAngle;

//        Logger.log(angleOfPoint+" : angleOfPoint,"+absoluteStartAngle+"absoluteStartAngle");
//        Logger.log(newAngle+" : newAngle");
        Logger.log(deviation+" : deviation");
        if(newAngle >=0) {
            if((newAngle <=MAX_ANGLE) && (newAngle < circleView.getEndAngle())){
                circleView.setStartAngle(newAngle);
                outlineCircle.setStartAngle(newAngle);
                updateStartAngleHandle();
                updateOuterRadiusHandle();
                updateInnerRadiusHandle();
            }
        }else{
            circleView.setStartAngle(0);
            outlineCircle.setStartAngle(0);
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
        double innerX=event.getX();//+innerRadiusHandle.getTranslateX();
        double innerY=event.getY();//+innerRadiusHandle.getTranslateY();
        double newInnerRadius= MathUtil.distance(centerX,centerY,innerX,innerY);
        double angleOfPoint=MathUtil.angleOfPoint(centerX,centerY,innerX,innerY);
        if(newInnerRadius<circleView.getOuterRadius()){
            circleView.setInnerRadius(newInnerRadius);
            outlineCircle.setInnerRadius(newInnerRadius);
            updateInnerRadiusHandle();
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
