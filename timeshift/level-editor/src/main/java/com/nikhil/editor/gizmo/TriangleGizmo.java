package com.nikhil.editor.gizmo;

import com.nikhil.view.item.TriangleView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 
 * Gizmo widget that allows customizing a triangle from the front end.
 * A TriangleGizmoDelegate provides mechanism for handling all the callbacks
 * Created by NikhilVerma on 06/09/15.
 */
public class TriangleGizmo extends Gizmo{
    
    public static final int MIN_BASE_WIDTH = 20;
    public static final int MIN_HEIGHT = 20;

    private TriangleView triangleView;
    private TriangleView outlineIsoscelesTriangle;

    private Circle rightHandle;
    private Circle leftHandle;
    private Circle tipHandle;

    public TriangleGizmo(TriangleView triangleView) {
        this.triangleView = triangleView;
        initView();
    }

    public void initView(){

        outlineIsoscelesTriangle=new TriangleView(triangleView);
        outlineIsoscelesTriangle.setFill(null);
        outlineIsoscelesTriangle.setStroke(Color.BLACK);
        outlineIsoscelesTriangle.getStrokeDashArray().add(7d);
        this.getChildren().add(outlineIsoscelesTriangle);

        leftHandle=getGenericHandle();
        this.getChildren().add(leftHandle);
        updateLeftHandle();

        rightHandle=getGenericHandle();
        this.getChildren().add(rightHandle);
        updateRightHandle();
        
        
        tipHandle=getGenericHandle();
        this.getChildren().add(tipHandle);
        updateTipHandle();

    }

    public void updateLeftHandle() {
        double x= 0;//triangleView.getTranslationX();
        double y= 0;//triangleView.getTranslationY();
        Point2D leftPoint= triangleView.getLeftPoint();
        leftHandle.setCenterX(leftPoint.getX()+x);
        leftHandle.setCenterY(leftPoint.getY()+y);

    }

    public void updateRightHandle() {
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        Point2D rightPoint= triangleView.getRightPoint();
        rightHandle.setCenterX(rightPoint.getX()+x);
        rightHandle.setCenterY(rightPoint.getY()+y);

    }

    public void updateTipHandle() {
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        Point2D tipPoint= triangleView.getTipPoint();
        tipHandle.setCenterX(tipPoint.getX()+x);
        tipHandle.setCenterY(tipPoint.getY()+y);

    }
    
    @Override
    public TriangleView getOutline() {
        return outlineIsoscelesTriangle;
    }

    @Override
    public void updateView() {
        
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
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        double ex=event.getX();
        double ey=event.getY();
        double newHeight=Math.abs(ey - y);
        if(newHeight> MIN_HEIGHT){
//            double angle=Math.toDegrees(Math.atan(newHeight / (triangleView.getBase() / 2)));
//            double tipAngle= (180-90-angle)*2;

//            triangleView.setTipAngle(tipAngle);
//            outlineIsoscelesTriangle.setTipAngle(tipAngle);
            triangleView.setHeight(newHeight);
            outlineIsoscelesTriangle.setHeight(newHeight);
            updateTipHandle();
        }
        event.consume();
    }

    private void tweakBaseWidth(MouseEvent event) {
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        double ex=event.getX();
        double ey=event.getY();
        double newBase=2*(Math.abs(ex-x));
        if(newBase> MIN_BASE_WIDTH){
            triangleView.setBase(newBase);
            outlineIsoscelesTriangle.setBase(newBase);
            updateLeftHandle();
            updateRightHandle();
        }
        event.consume();
    }
}
