package com.nikhil.editor.gizmo;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Base class for every gizmo.
 * Created by NikhilVerma on 06/12/15.
 */
public abstract class Gizmo extends Group implements EventHandler<MouseEvent> {

    public static final int HANDLE_RADIUS = 3;
    public static final double OUTLINE_STROKE_DASH=7;
    public static final Color OUTLINE_COLOR=Color.BLACK;

    public Circle getGenericHandle(){
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
                    if(node==getOutline()){
                        node.setVisible(true);
                    }else{
                        node.setVisible(false);
                    }
                }
                break;
            case SHOW_ONLY_HANDLE:
                this.setVisible(true);
                for(Node node:this.getChildren()){
                    if(node==getOutline()){
                        node.setVisible(false);
                    }else{
                        node.setVisible(true);
                    }
                }
                break;
        }
    }

    /**@return outline node that strokes this view with a dotted line*/
    public abstract Node getOutline();

    /**Updates the gizmo view*/
    public abstract void updateView();
}
