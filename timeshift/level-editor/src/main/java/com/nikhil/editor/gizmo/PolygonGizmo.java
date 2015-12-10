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
public class PolygonGizmo extends Gizmo{

    private PolygonView polygonView;
    private PolygonView outlinePolygon;

    private List<Circle> pointHandles=new ArrayList<Circle>();

    private double initialX;
    private double initialY;

    public PolygonGizmo(PolygonView polygonView) {
        this.polygonView = polygonView;
        initView();
        showGizmo(GizmoVisibilityOption.HIDE_ALL);
    }

    private void initView(){

        outlinePolygon=new PolygonView(polygonView);
        outlinePolygon.setFill(null);
        outlinePolygon.setStroke(OUTLINE_COLOR);
        outlinePolygon.getStrokeDashArray().add(OUTLINE_STROKE_DASH);

        this.getChildren().add(outlinePolygon);

        for(UtilPoint point2D: polygonView.getPolygonPoints()){
            Circle pointHandle=getGenericHandle();
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

    @Override
    public void updateView(){
        outlinePolygon.setOriginRotate(polygonView.getOriginRotate());
        int index=0;
        for(UtilPoint point2D: polygonView.getPolygonPoints()){
            //update the handle
            UtilPoint rotatedPoint = polygonView.rotatedAroundOrigin(point2D);
            Circle pointHandle = pointHandles.get(index);
            pointHandle.setCenterX(rotatedPoint.getX());
            pointHandle.setCenterY(rotatedPoint.getY());

            //update the point on the outline too
            outlinePolygon.updatePoint(index,point2D);
            index++;
        }
        this.setLayoutX(polygonView.getLayoutX());
        this.setLayoutY(polygonView.getLayoutY());
        //TODO work on syncing scale without affecting size of handles
        this.setScaleX(polygonView.getScaleX());
        this.setScaleY(polygonView.getScaleY());
//        this.setRotate(polygonView.getOriginRotate());
    }

    @Override
    public Node getOutline() {
        return outlinePolygon;
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

//            UtilPoint relativePoint = new UtilPoint(relativeX, relativeY);
            //unrotate because relative x and relative y dont concern themselves with the origin angle
            UtilPoint relativePoint =polygonView.unRotatedAroundOrigin(relativeX,relativeY);
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

}
