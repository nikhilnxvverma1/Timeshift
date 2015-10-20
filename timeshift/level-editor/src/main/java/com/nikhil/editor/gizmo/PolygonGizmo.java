package com.nikhil.editor.gizmo;

import com.nikhil.math.MathUtil;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.PolygonView;
import com.nikhil.view.item.delegate.PolygonViewDelegate;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikhilVerma on 06/09/15.
 */
public class PolygonGizmo extends Group implements EventHandler<MouseEvent> {

    public static final int HANDLE_RADIUS = 3;

    private PolygonView polygonView;
    private PolygonView outlinePolygon;

    private List<Circle> pointHandles=new ArrayList<Circle>();

    private double initialX;
    private double initialY;

    public PolygonGizmo(PolygonView polygonView) {
        this.polygonView = polygonView;
        showGizmo(GizmoVisibilityOption.HIDE_ALL);
    }

    public void initializeView(){

        outlinePolygon=new PolygonView(polygonView);
        outlinePolygon.setFill(null);
        outlinePolygon.setStroke(Color.BLACK);
        outlinePolygon.getStrokeDashArray().add(7d);

        this.getChildren().add(outlinePolygon);

        for(UtilPoint point2D: polygonView.getPolygonPoints()){
            Circle pointHandle=getGenericHandle();
            //handles should come rotated if the original polygon is rotated by some example
//            UtilPoint rotatedPoint = MathUtil.getRotatedPoint(point2D, polygonView.getRotate());
//            UtilPoint transformedPoint = rotatedPoint.multiply(polygonView.getScale());
//            pointHandle.setCenterX(transformedPoint.getX());
//            pointHandle.setCenterY(transformedPoint.getY());
//            pointHandle.setCenterX(0);
//            pointHandle.setCenterY(0);

            //storing location in center later helps us in event handling while tweaking this point
            pointHandle.setCenterX(point2D.getX());
            pointHandle.setCenterY(point2D.getY());
            pointHandles.add(pointHandle);
            this.getChildren().add(pointHandle);
        }
        //the parent group takes care of the scale,rotation,translation
        //therefore this outline will always be at 0,0
        outlinePolygon.setLayoutX(0);
        outlinePolygon.setLayoutY(0);
        outlinePolygon.setRotate(0);
        outlinePolygon.setScale(1);

        //TODO consider making a single call to updateView()
        this.setLayoutX(polygonView.getLayoutX());
        this.setLayoutY(polygonView.getLayoutY());
        this.setScaleX(polygonView.getScaleX());
        this.setScaleY(polygonView.getScaleY());
        this.setRotate(polygonView.getRotate());


    }

    public void updateView(){
        int index=0;
        for(UtilPoint point2D: polygonView.getPolygonPoints()){
            Circle pointHandle = pointHandles.get(index);
            //handles should come rotated if the original polygon is rotated by some example
//            UtilPoint rotatedPoint = MathUtil.getRotatedPoint(point2D, polygonView.getRotate());
//            UtilPoint transformedPoint = rotatedPoint.multiply(polygonView.getScale());
//            pointHandle.setCenterX(transformedPoint.getX());
//            pointHandle.setCenterY(transformedPoint.getY());
//            pointHandle.setCenterX(0);
//            pointHandle.setCenterY(0);
            pointHandle.setCenterX(point2D.getX());
            pointHandle.setCenterY(point2D.getY());
            outlinePolygon.updatePoint(index,point2D);
            index++;
        }
        this.setLayoutX(polygonView.getLayoutX());
        this.setLayoutY(polygonView.getLayoutY());
        //TODO work on syncing scale without affecting size of handles
        this.setScaleX(polygonView.getScaleX());
        this.setScaleY(polygonView.getScaleY());
        this.setRotate(polygonView.getRotate());
    }

    private Circle getGenericHandle(){
        Circle genericHandle=new Circle();
        genericHandle.setRadius(HANDLE_RADIUS);
        genericHandle.setFill(Color.WHITE);
        genericHandle.setStroke(Color.BLACK);
        genericHandle.addEventHandler(MouseEvent.MOUSE_PRESSED,this);
        genericHandle.addEventHandler(MouseEvent.MOUSE_DRAGGED,this);
        genericHandle.addEventHandler(MouseEvent.MOUSE_RELEASED,this);
        genericHandle.setCursor(Cursor.HAND);
        return genericHandle;
    }

    @Override
    public void handle(MouseEvent event) {

        if(event.getEventType()==MouseEvent.MOUSE_PRESSED){
            captureInitialLocationOfPoint(event);
        }else if(event.getEventType()==MouseEvent.MOUSE_DRAGGED){
            tweakPolygonPoint(event);
        }else if(event.getEventType()==MouseEvent.MOUSE_RELEASED){
            notifyChangesToDelegate(event);
        }
    }

    private void captureInitialLocationOfPoint(MouseEvent event) {
        EventTarget eventTarget=event.getTarget();
        int index=pointHandles.indexOf(eventTarget);
        UtilPoint point2D = polygonView.getPolygonPoints().get(index);
        initialX=point2D.getX();
        initialY=point2D.getY();
        event.consume();//because polygon tool might register this event and create a point
    }

    private void tweakPolygonPoint(MouseEvent event) {
        EventTarget eventTarget=event.getTarget();
        int index=pointHandles.indexOf(eventTarget);
        if(index!=-1){
            Circle handle=(Circle)eventTarget;
            double relativeX=event.getX();
            double relativeY=event.getY();

            UtilPoint relativePoint = new UtilPoint(relativeX, relativeY);
            polygonView.updatePoint(index, relativePoint);
            outlinePolygon.updatePoint(index, relativePoint);
            handle.setCenterX(relativeX);
            handle.setCenterY(relativeY);
            polygonView.getDelegate().propertyCurrentlyGettingTweaked();
        }
        event.consume();
    }

    private void notifyChangesToDelegate(MouseEvent event){
        EventTarget eventTarget=event.getTarget();
        int index=pointHandles.indexOf(eventTarget);
        if(index!=-1&&
                polygonView.getDelegate()!=null){
            polygonView.getDelegate().finishedTweakingPolygonPoint(index, initialX,initialY);
        }
        event.consume();
    }

    public void showGizmo(GizmoVisibilityOption visibilityOption){
        switch (visibilityOption){

            case HIDE_ALL:
                this.setVisible(false);
                break;
            case SHOW_ALL:
                this.setVisible(true);
                for(Node node:this.getChildren()){
                    node.setVisible(true);
                }
                break;
            case SHOW_ONLY_OUTLINE:
                this.setVisible(true);
                for(Node node:this.getChildren()){
                    if(node==outlinePolygon){
                        node.setVisible(true);
                    }else{
                        node.setVisible(false);
                    }
                }
                break;
            case SHOW_ONLY_HANDLE:
                this.setVisible(true);
                for(Node node:this.getChildren()){
                    if(node==outlinePolygon){
                        node.setVisible(false);
                    }else{
                        node.setVisible(true);
                    }
                }
                break;
        }
    }
}
