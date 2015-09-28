package com.nikhil.controller;

import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.delegate.ItemViewDelegate;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ShapeViewController extends ItemViewController {

    public ShapeViewController(Workspace workspace) {
        super(workspace);
    }

    /**
     * Gets the rotated point after rotating it by the shape's angle
     * around the shapes anchor point
     *
     * @param x x in local space of shape
     * @param y y in local space of shape
     * @return point in local space of shape but rotated by the shape's angle
     */
    public UtilPoint getNormalizedPoint(double x,double y){
        //rotate around by the rotation
        double theta = Math.toRadians(getRotation());
        //formula for rotation
        //x'=x*cos(theta)-y*sin(theta)
        //y'=x*sin(theta)+y*cos(theta)
        double nx=x*Math.cos(theta)-y*Math.sin(theta);
        double ny=y*Math.sin(theta)+y*Math.cos(theta);
        return new UtilPoint(nx,ny);
    }

    @Override
    public boolean rotateBy(double dAngle) {
        //TODO change rotation component
        return false;
    }

    @Override
    public boolean scaleBy(double dScale) {
        //TODO change scale component
        return false;
    }

    public abstract UtilPoint getTranslation();

    public abstract double getScale();

    public abstract double getRotation();

    public UtilPoint getAnchorPoint(){
        Bounds layoutBoundsInWorksheet = getLayoutBoundsInWorksheet();
        return new UtilPoint(layoutBoundsInWorksheet.getWidth()/2,layoutBoundsInWorksheet.getHeight()/2);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //capture the location and just consume the event
        //so that workspace doesn't clear selection
        UtilPoint normalized=getNormalizedPoint(mouseEvent.getX(),mouseEvent.getY());
        workspace.getSelectedItems().captureTemporaryValues(normalized.getX(), normalized.getY());
//        workspace.getSelectedItems().captureTemporaryValues(mouseEvent.getX(), mouseEvent.getY());
        mouseEvent.consume();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        SelectedItems selectedItems = workspace.getSelectedItems();
        boolean selected = selectedItems.contains(this);
        if(!selected){
            selectedItems.requestFocus(this, mouseEvent.isShiftDown());
        }

        UtilPoint normalized=getNormalizedPoint(mouseEvent.getX(),mouseEvent.getY());
        double dx = normalized.getX()-selectedItems.getTemporaryValue1();
        double dy = normalized.getY()-selectedItems.getTemporaryValue2();
//        double dx = mouseEvent.getX()-selectedItems.getTemporaryValue1();
//        double dy = mouseEvent.getY()-selectedItems.getTemporaryValue2();
        selectedItems.moveSelectionBy(dx, dy);
        mouseEvent.consume();

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if(workspace.getSelectedItems().contains(this)){
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            boolean moveCommandIssued = workspace.getSelectedItems().finishMovingSelection();
            if(!moveCommandIssued){
                workspace.getSelectedItems().requestFocus(this,mouseEvent.isShiftDown());
                if(mouseEvent.getClickCount() ==2){
                    Logger.log("Focusing in detail");
                    workspace.getSelectedItems().requestFocusInDetail(this);
                }
            }
        } else {
            workspace.getSelectedItems().requestFocus(this, mouseEvent.isShiftDown());
        }
        mouseEvent.consume();
    }

    @Override
    public void propertyCurrentlyGettingTweaked() {
        workspace.getSelectedItems().updateView();
    }
}
