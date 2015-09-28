package com.nikhil.editor.gizmo;

import com.nikhil.math.MathUtil;
import com.nikhil.view.item.CircleView;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Gizmo widget that allows customizing a circle from the front end.
 * A CircleGizmoDelegate provides mechanism for handling all the callbacks
 * Created by NikhilVerma on 30/08/15.
 */
public class CircleGizmo implements EventHandler<MouseEvent>{

    public static final int HANDLE_RADIUS = 3;

    //TODO have a delegate for all the tweaking callbacks
    private CircleView circleView;
//    private Group outline;//TODO might not be needed at all
    private CircleView outlineCircle;
    private Circle innerRadiusHandle;
    private Circle outerRadiusHandle;
    private Circle startAngleHandle;
    private Circle endAngleHandle;

    public CircleGizmo(Pane pane,CircleView circleView){
        this.circleView=circleView;
//        outline=new Group();
//        outline.getChildren().add(circleView.getPath());
        initializeGraphics(pane);
        updateView();
    }

    private void initializeGraphics(Pane pane){
        outlineCircle=new CircleView(circleView);
        outlineCircle.getPath().setFill(null);
        //TODO define constants somewhere
        outlineCircle.getPath().setStroke(Color.BLUE);
        outlineCircle.getPath().getStrokeDashArray().add(7d);
        pane.getChildren().add(outlineCircle.getPath());

        innerRadiusHandle=getGenericHandle();
        pane.getChildren().add(innerRadiusHandle);

        outerRadiusHandle=getGenericHandle();
        pane.getChildren().add(outerRadiusHandle);

        startAngleHandle=getGenericHandle();
        pane.getChildren().add(startAngleHandle);

        endAngleHandle=getGenericHandle();
        pane.getChildren().add(endAngleHandle);
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

    public void updateView(){
        outlineCircle.copyValuesFrom(circleView);
        updateInnerRadiusHandle();
        updateOuterRadiusHandle();
        updateStartAngleHandle();
        updateEndAngleHandle();
    }

    private void updateEndAngleHandle() {
        double handleRadius=endAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double totalRadius=handleRadius+circleView.getOuterRadius();
        double angle=circleView.getEndAngle()-handleReductionAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius+circleView.getTranslationX();
        double y=Math.sin(angle)*totalRadius+circleView.getTranslationY();
        endAngleHandle.setCenterX(x);
        endAngleHandle.setCenterY(y);
    }

    private void updateStartAngleHandle() {
        double handleRadius=startAngleHandle.getRadius();
        double handleElevationAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double totalRadius=handleRadius+circleView.getOuterRadius();
        double angle=circleView.getStartAngle()+handleElevationAngle;
        angle=Math.toRadians(angle);
        double x=Math.cos(angle)*totalRadius+circleView.getTranslationX();
        double y=Math.sin(angle)*totalRadius+circleView.getTranslationY();
        startAngleHandle.setCenterX(x);
        startAngleHandle.setCenterY(y);
    }

    private void updateInnerRadiusHandle() {
        double averageAngle=(circleView.getStartAngle()+circleView.getEndAngle())/2;
        averageAngle=Math.toRadians(averageAngle);

        double ix=Math.cos(averageAngle)*circleView.getInnerRadius()+circleView.getTranslationX();
        double iy=Math.sin(averageAngle)*circleView.getInnerRadius()+circleView.getTranslationY();
        innerRadiusHandle.setCenterX(ix);
        innerRadiusHandle.setCenterY(iy);
    }

    private void updateOuterRadiusHandle() {
        double averageAngle=(circleView.getStartAngle()+circleView.getEndAngle())/2;
        averageAngle=Math.toRadians(averageAngle);
        double ox=Math.cos(averageAngle)*circleView.getOuterRadius()+circleView.getTranslationX();
        double oy=Math.sin(averageAngle)*circleView.getOuterRadius()+circleView.getTranslationY();
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
        double centerX=circleView.getTranslationX();
        double centerY=circleView.getTranslationY();
        double angleX=event.getX()+endAngleHandle.getTranslateX();
        double angleY=event.getY()+endAngleHandle.getTranslateY();
        double angle=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
        angle+=handleReductionAngle;//discount the small angle shift created for the handle
        System.out.println("end angel to possibly set : "+angle);
        if(angle>circleView.getStartAngle()){
            circleView.setEndAngle(angle);
            outlineCircle.setEndAngle(angle);
            updateEndAngleHandle();
            updateOuterRadiusHandle();
            updateInnerRadiusHandle();
        }
        event.consume();
    }

    private void tweakStartAngle(MouseEvent event) {
        double handleRadius=startAngleHandle.getRadius();
        double handleReductionAngle=Math.toDegrees(Math.atan(handleRadius / circleView.getOuterRadius()));
        double centerX=circleView.getTranslationX();
        double centerY=circleView.getTranslationY();
        double angleX=event.getX()+startAngleHandle.getTranslateX();
        double angleY=event.getY()+startAngleHandle.getTranslateY();
        double angle=MathUtil.angleOfPoint(centerX,centerY,angleX,angleY);
        angle+=handleReductionAngle;//discount the small angle shift created for the handle
        System.out.println("end angel to possibly set : "+angle);
        if(angle<circleView.getEndAngle()){
            circleView.setStartAngle(angle);
            outlineCircle.setStartAngle(angle);
            updateStartAngleHandle();
            updateOuterRadiusHandle();
            updateInnerRadiusHandle();
        }
        event.consume();
    }

    private void tweakInnerRadius(MouseEvent event) {
        double centerX=circleView.getTranslationX();
        double centerY=circleView.getTranslationY();
        double innerX=event.getX()+innerRadiusHandle.getTranslateX();
        double innerY=event.getY()+innerRadiusHandle.getTranslateY();
        double newInnerRadius= MathUtil.distance(centerX,centerY,innerX,innerY);
        if(newInnerRadius<circleView.getOuterRadius()){
            circleView.setInnerRadius(newInnerRadius);
            outlineCircle.setInnerRadius(newInnerRadius);
            updateInnerRadiusHandle();
//            innerRadiusHandle.setCenterX(innerX);
//            innerRadiusHandle.setCenterY(innerY);
            //TODO notify delegate

        }
        event.consume();
    }

    private void tweakOuterRadius(MouseEvent event){

        double centerX=circleView.getTranslationX();
        double centerY=circleView.getTranslationY();
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
            //TODO notify delegate

        }
        event.consume();
    }
}
