package com.nikhil.editor.gizmo;

import com.nikhil.view.item.IsoscelesTriangleView;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by NikhilVerma on 06/09/15.
 */
public class IsoscelesTriangleGizmo implements EventHandler<MouseEvent> {

    public static final int HANDLE_RADIUS = 3;
    public static final int MIN_BASE_WIDTH = 20;
    public static final int MIN_HEIGHT = 20;

    private IsoscelesTriangleView isoscelesTriangleView;
    private IsoscelesTriangleView outlineIsoscelesTriangle;

    private Circle rightHandle;
    private Circle leftHandle;
    private Circle tipHandle;

    public IsoscelesTriangleGizmo(Pane pane,IsoscelesTriangleView isoscelesTriangleView) {
        this.isoscelesTriangleView = isoscelesTriangleView;
        initializeGraphics(pane);
    }

    public void initializeGraphics(Pane pane){

        outlineIsoscelesTriangle=new IsoscelesTriangleView(isoscelesTriangleView);
        outlineIsoscelesTriangle.setFill(null);
        outlineIsoscelesTriangle.setStroke(Color.BLACK);
        outlineIsoscelesTriangle.getStrokeDashArray().add(7d);
        pane.getChildren().add(outlineIsoscelesTriangle);

        leftHandle=getGenericHandle();
        pane.getChildren().add(leftHandle);
        updateLeftHandle();

        rightHandle=getGenericHandle();
        pane.getChildren().add(rightHandle);
        updateRightHandle();
        
        
        tipHandle=getGenericHandle();
        pane.getChildren().add(tipHandle);
        updateTipHandle();

    }

    public void updateLeftHandle() {
        double x=isoscelesTriangleView.getTranslationX();
        double y=isoscelesTriangleView.getTranslationY();
        Point2D leftPoint=isoscelesTriangleView.getLeftPoint();
        leftHandle.setCenterX(leftPoint.getX()+x);
        leftHandle.setCenterY(leftPoint.getY()+y);

    }

    public void updateRightHandle() {
        double x=isoscelesTriangleView.getTranslationX();
        double y=isoscelesTriangleView.getTranslationY();
        Point2D rightPoint=isoscelesTriangleView.getRightPoint();
        rightHandle.setCenterX(rightPoint.getX()+x);
        rightHandle.setCenterY(rightPoint.getY()+y);

    }

    public void updateTipHandle() {
        double x=isoscelesTriangleView.getTranslationX();
        double y=isoscelesTriangleView.getTranslationY();
        Point2D tipPoint=isoscelesTriangleView.getTipPoint();
        tipHandle.setCenterX(tipPoint.getX()+x);
        tipHandle.setCenterY(tipPoint.getY()+y);

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

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==rightHandle){
            tweakBaseWidth(event);
        }else if(event.getTarget()==leftHandle){
            tweakBaseWidth(event);
        }else if(event.getTarget()==tipHandle){
            tweakTipAngle(event);
        }
    }

    private void tweakTipAngle(MouseEvent event) {
        double x=isoscelesTriangleView.getTranslationX();
        double y=isoscelesTriangleView.getTranslationY();
        double ex=event.getX();
        double ey=event.getY();
        double newHeight=Math.abs(ey - y);
        if(newHeight> MIN_HEIGHT){
//            double angle=Math.toDegrees(Math.atan(newHeight / (isoscelesTriangleView.getBase() / 2)));
//            double tipAngle= (180-90-angle)*2;

//            isoscelesTriangleView.setTipAngle(tipAngle);
//            outlineIsoscelesTriangle.setTipAngle(tipAngle);
            isoscelesTriangleView.setHeight(newHeight);
            outlineIsoscelesTriangle.setHeight(newHeight);
            updateTipHandle();
        }
        event.consume();
    }

    private void tweakBaseWidth(MouseEvent event) {
        double x=isoscelesTriangleView.getTranslationX();
        double y=isoscelesTriangleView.getTranslationY();
        double ex=event.getX();
        double ey=event.getY();
        double newBase=2*(Math.abs(ex-x));
        if(newBase> MIN_BASE_WIDTH){
            isoscelesTriangleView.setBase(newBase);
            outlineIsoscelesTriangle.setBase(newBase);
            updateLeftHandle();
            updateRightHandle();
        }
        event.consume();
    }
}
