package com.nikhil.editor.gizmo;

import com.nikhil.math.MathUtil;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.TriangleView;
import com.nikhil.view.item.delegate.TriangleViewDelegate;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 
 * Gizmo widget that allows customizing a triangle from the front end.
 * A {@link TriangleViewDelegate} provides mechanism for handling all the callbacks
 * Created by NikhilVerma on 06/09/15.
 */
public class TriangleGizmo extends Gizmo{

    /** Used internally to capture the initial value across press-drag-release events*/
    private static double initialValue;
    public static final int MIN_BASE_WIDTH = 20;
    public static final int MIN_HEIGHT = 20;

    private TriangleView triangleView;
    private TriangleView outlineTriangle;

    private Circle rightHandle;
    private Circle leftHandle;
    private Circle tipHandle;

    public TriangleGizmo(TriangleView triangleView) {
        this.triangleView = triangleView;
        initView();
        showGizmo(GizmoVisibilityOption.HIDE_ALL);
    }

    public void initView(){

        outlineTriangle =new TriangleView(triangleView);
        outlineTriangle.setFill(null);
        outlineTriangle.setStroke(OUTLINE_COLOR);
        outlineTriangle.getStrokeDashArray().add(OUTLINE_STROKE_DASH);
        outlineTriangle.setLayoutX(0);
        outlineTriangle.setLayoutY(0);
        outlineTriangle.setScale(1);
        outlineTriangle.setRotate(0);

        this.getChildren().add(outlineTriangle);

        leftHandle=getGenericHandle();
        this.getChildren().add(leftHandle);

        rightHandle=getGenericHandle();
        this.getChildren().add(rightHandle);

        tipHandle=getGenericHandle();
        this.getChildren().add(tipHandle);

        updateView();

    }

    public void updateLeftHandle() {
        double x= 0;//triangleView.getTranslationX();
        double y= 0;//triangleView.getTranslationY();
        UtilPoint leftPoint= triangleView.getLeftPoint();
        leftHandle.setCenterX(leftPoint.getX()+x);
        leftHandle.setCenterY(leftPoint.getY()+y);

    }

    public void updateRightHandle() {
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        UtilPoint rightPoint= triangleView.getRightPoint();
        rightHandle.setCenterX(rightPoint.getX()+x);
        rightHandle.setCenterY(rightPoint.getY()+y);

    }

    public void updateTipHandle() {
        double x=0;// triangleView.getTranslationX();
        double y=0;// triangleView.getTranslationY();
        UtilPoint tipPoint= triangleView.getTipPoint();
        tipHandle.setCenterX(tipPoint.getX()+x);
        tipHandle.setCenterY(tipPoint.getY()+y);

    }
    
    @Override
    public TriangleView getOutline() {
        return outlineTriangle;
    }

    @Override
    public void updateView() {
        this.setLayoutX(triangleView.getLayoutX());
        this.setLayoutY(triangleView.getLayoutY());
        outlineTriangle.copyValuesFrom(triangleView);
        updateRightHandle();
        updateLeftHandle();
        updateTipHandle();
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getTarget()==rightHandle){
            tweakBase(event);
        }else if(event.getTarget()==leftHandle){
            tweakBase(event);
        }else if(event.getTarget()==tipHandle){
            tweakHeight(event);
        }
    }

    private void tweakHeight(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=triangleView.getHeight();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            triangleView.getDelegate().finishedTweakingHeight(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            double y=0;// triangleView.getTranslationY();
            double ey=event.getY();
            double newHeight= MathUtil.abs(ey - y)*2;
            double oldHeight=triangleView.getHeight();
            if(newHeight> MIN_HEIGHT){
                triangleView.setHeight(newHeight);
                updateView();
                triangleView.getDelegate().tweakingHeight(oldHeight,newHeight);
            }
        }
        triangleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }

    private void tweakBase(MouseEvent event) {
        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            initialValue=triangleView.getBase();
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            triangleView.getDelegate().finishedTweakingBase(initialValue);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){

            double x=0;// triangleView.getTranslationX();
            double ex=event.getX();
            double newBase=2*(Math.abs(ex-x));
            double oldBase=triangleView.getBase();
            if(newBase> MIN_BASE_WIDTH){
                triangleView.setBase(newBase);
                updateView();
                triangleView.getDelegate().tweakingBase(oldBase,newBase);
            }
        }
        triangleView.getDelegate().propertyCurrentlyGettingTweaked();
        event.consume();
    }
}
