package com.nikhil.controller;

import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.timeline.change.spatial.SpatialChangeHandler;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalChangeHandler;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.record.Metadata;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ShapeViewController extends ItemViewController implements TemporalChangeHandler,SpatialChangeHandler {

    public static final int SCALE_INDEX=0;
    public static final int ROTATION_INDEX=1;
    public static final int TRANSLATION_INDEX=2;
    public static final int ANCHOR_POINT_INDEX=3;
    protected TreeItem<Metadata> metadataTree;

    public ShapeViewController(CompositionViewController compositionViewController) {
        super(compositionViewController);
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

    /**
     * Sets this controller as the change handler for the model.
     * This class should be called by the subclass controller a the end after
     * initialization. <b>Subclassing Note</b>: make sure to call super.setSelfAsChangeHandler()
     * first so as to set change handlers for basic shape properties (S.R.T etc)
     */
    protected void setSelfAsChangeHandler(){
        ShapeModel shapeModel = getShapeModel();
        shapeModel.anchorPointChange().setChangeHandler(this);
        shapeModel.translationChange().setChangeHandler(this);
        shapeModel.scaleChange().setChangeHandler(this);
        shapeModel.rotationChange().setChangeHandler(this);
    }

    /**
     * Each subclass of shape view controller is supposed to return the shape model
     * associated with the shape view controller
     * @return the model shape that the subclass controller represents
     */
    protected abstract ShapeModel getShapeModel();

    @Override
    public UtilPoint getTranslation(){
        Shape itemView = getItemView();
        return new UtilPoint(itemView.getLayoutX(),itemView.getLayoutY());
    }

    public double getScale(){
        return getItemView().getScaleX();
    }

    public double getRotation(){
        return getItemView().getRotate();
    }

    /**
     * Uses the view to create a shape model
     * and corresponding model controller
     */
    protected abstract void constructModelControllerUsingView();

    @Override
    public void setCompositionViewController(CompositionViewController compositionViewController) {
        //we do some processing if the composition controller changes
        super.setCompositionViewController(compositionViewController);
        constructModelControllerUsingView();//recomputes the model controller based on the new workspace
    }

    @Override
    public boolean contains(double x, double y) {
        return getItemView().contains(x,y);
    }

    @Override
    public void moveTo(double newX, double newY) {
        //change translation component of the view and gizmo
//        double x=polygonView.getLayoutX();
//        double y=polygonView.getLayoutY();
//        double newX = x + dx;
//        double newY = y + dy;
        getItemView().setLayoutX(newX);
        getItemView().setLayoutY(newY);
        getGizmo().updateView();

        //convert to work point and update the business model
        Workspace workspace=compositionViewController.getWorkspace();
        double workPointX = workspace.workPointX(newX);
        double workPointY = workspace.workPointY(newY);
        getShapeModel().setTranslation(new UtilPoint(workPointX, workPointY));
    }

    @Override
    public double scaleBy(double dScale) {

        //change scale component of the view and gizmo
        double oldScale = getItemView().getScaleX();
        double newScale=dScale+ oldScale;
        if(newScale<0.1){
            return getItemView().getScaleX();
        }
        getItemView().setScaleX(newScale);
        getGizmo().updateView();

        //TODO convert to work point scale and update the business model
        double workScale=newScale;
        getShapeModel().setScale(workScale);
        return getScale();
    }

    @Override
    public double rotateBy(double dAngle) {
        //change rotation component of the view and gizmo
        double oldRotation= getItemView().getRotate();
        double newRotation=dAngle+ oldRotation;
        newRotation= MathUtil.under360(newRotation);
        getItemView().setRotate(newRotation);
        getGizmo().updateView();

        //TODO convert to work point scale and update the business model
        double workRotation=newRotation;
        getShapeModel().setRotation(workRotation);
        return newRotation;
    }

    public UtilPoint getAnchorPoint(){
        Bounds layoutBoundsInWorksheet = getLayoutBoundsInWorksheet();
        return new UtilPoint(layoutBoundsInWorksheet.getWidth()/2,layoutBoundsInWorksheet.getHeight()/2);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if(!isInteractive()){
            return;
        }
        //capture the location and just consume the event
        //so that workspace doesn't clear selection
        UtilPoint normalized=getNormalizedPoint(mouseEvent.getX(),mouseEvent.getY());
        compositionViewController.getWorkspace().getSelectedItems().captureTemporaryValues(normalized.getX(), normalized.getY());
//        workspace.getSelectedItems().captureTemporaryValues(mouseEvent.getX(), mouseEvent.getY());
        mouseEvent.consume();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if(!isInteractive()){
            return;
        }
        SelectedItems selectedItems = compositionViewController.getWorkspace().getSelectedItems();
        boolean selected = selectedItems.contains(this);
        if(!selected){
            selectedItems.requestFocus(this, mouseEvent.isShiftDown());
        }

        UtilPoint normalized=getNormalizedPoint(mouseEvent.getX(),mouseEvent.getY());
        double dx = normalized.getX()-selectedItems.getTemporaryValue1();
        double dy = normalized.getY()-selectedItems.getTemporaryValue2();
        selectedItems.moveSelectionBy(dx, dy);
        mouseEvent.consume();

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if(!isInteractive()){
            return;
        }
        Workspace workspace=compositionViewController.getWorkspace();
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
        compositionViewController.getWorkspace().getSelectedItems().updateView();
    }

    @Override
    public void valueChanged(SpatialKeyframeChangeNode changeNode) {
        ShapeModel shapeModel = getShapeModel();
        Shape shapeView = getItemView();
        if(changeNode==shapeModel.translationChange()){
            shapeView.setLayoutX(changeNode.getCurrentPoint().getX());
            shapeView.setLayoutY(changeNode.getCurrentPoint().getY());
        }else if(changeNode==shapeModel.anchorPointChange()){
            shapeView.setTranslateX(changeNode.getCurrentPoint().getX());
            shapeView.setTranslateY(changeNode.getCurrentPoint().getY());
        }
    }

    @Override
    public void valueChanged(TemporalKeyframeChangeNode changeNode) {
        ShapeModel shapeModel = getShapeModel();
        Shape shapeView = getItemView();
        if(changeNode==shapeModel.scaleChange()){
            shapeView.setScaleX(changeNode.getCurrentValue().get(0));// for all shapes, scale y is bind to scale x
        }else if(changeNode==shapeModel.rotationChange()){
            shapeView.setRotate(changeNode.getCurrentValue().get(0));
        }
    }
}
