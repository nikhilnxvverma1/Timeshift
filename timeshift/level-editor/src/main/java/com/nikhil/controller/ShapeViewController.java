package com.nikhil.controller;

import com.nikhil.command.item.RotateShape;
import com.nikhil.command.item.ScaleShape;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.model.shape.PolygonModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.spatial.SpatialChangeHandler;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalChangeHandler;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.item.ShapeView;
import com.nikhil.view.item.record.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Shape;

/**
 * Created by NikhilVerma on 30/08/15.
 */
public abstract class ShapeViewController extends ItemViewController implements TemporalChangeHandler,SpatialChangeHandler {

    public static final int SCALE_INDEX=0;
    public static final int ROTATION_INDEX=1;
    public static final int TRANSLATION_INDEX=2;
    public static final int ANCHOR_POINT_INDEX=3;
    /** Root tree item that contains the metadata tree. Subclasses should ensure to call initMetadataTree() */
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
        ShapeModel shapeModel = getItemModel();
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
    @Override
    public abstract ShapeModel getItemModel();

    @Override
    public abstract ShapeView getItemView() ;

    @Override
    public UtilPoint getTranslation(){
        Shape itemView = getItemView();
        return new UtilPoint(itemView.getLayoutX(),itemView.getLayoutY());
    }

    @Override
    public Bounds getLayoutBoundsInWorksheet() {
        return getItemView().getBoundsInParent();
    }

    /**
     * Initializes the metdata tree sublasses should call this method after making all the initializations.
     * Also subclasses that override this must call super()
     */
    protected void initMetadataTree(){

        ShapeModel shapeModel = getItemModel();

        //header
        TreeItem<Metadata> headerMeta= new TreeItem<>(
                new HeaderMetadata(shapeModel.getName(), MetadataTag.HEADER, this, true));

        //scale
        final TemporalMetadata scaleMeta = new TemporalMetadata(MetadataTag.SCALE,
                shapeModel.scaleChange(),
                this);
        scaleMeta.setValueNode(createScaleValueNode(scaleMeta));
        TreeItem<Metadata> scaleTreeItem= new TreeItem<>(scaleMeta);

        //rotation
        final TemporalMetadata rotationMeta = new TemporalMetadata(MetadataTag.ROTATION,
                shapeModel.rotationChange(),
                this);
        rotationMeta.setValueNode(createRotateValueNode(rotationMeta));
        TreeItem<Metadata> rotationTreeItem= new TreeItem<>(rotationMeta);

        //translation
        TreeItem<Metadata> translationTreeItem= new TreeItem<>(new SpatialMetadata(MetadataTag.TRANSLATION,
                shapeModel.translationChange(),
                this));

        //anchor point
        TreeItem<Metadata> anchorPointTreeItem= new TreeItem<>(new SpatialMetadata(MetadataTag.ANCHOR_POINT,
                shapeModel.anchorPointChange(),
                this));

        headerMeta.getChildren().add(SCALE_INDEX,scaleTreeItem);
        headerMeta.getChildren().add(ROTATION_INDEX,rotationTreeItem);
        headerMeta.getChildren().add(TRANSLATION_INDEX,translationTreeItem);
        headerMeta.getChildren().add(ANCHOR_POINT_INDEX,anchorPointTreeItem);
        metadataTree=headerMeta;
    }
    
    @Override
    public TreeItem<Metadata> getMetadataTree() {
        return metadataTree;
    }

    @Override
    public boolean overlapsWithSceneBounds(Bounds sceneBounds) {
        Bounds polygonBoundsInScene = getItemView().localToScene(getItemView().getLayoutBounds());
        return polygonBoundsInScene.intersects(sceneBounds);
    }

    public double getScale(){
        return getItemView().getScaleX();
    }

    public double getRotation(){
        return getItemView().getOriginRotate();
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
        getItemModel().setTranslation(new UtilPoint(workPointX, workPointY));
    }

    private HBox createScaleValueNode(TemporalMetadata metadata){

        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                double dScale = newValue - oldValue;
                double oldScale=ShapeViewController.this.getScale();
                double newScale=ShapeViewController.this.scaleBy(dScale);
                metadata.registerContinuousChange(new KeyValue(oldScale), new KeyValue(newScale));
                //update the outline
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                ScaleShape scaleShape =new ScaleShape(ShapeViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(scaleShape, !dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        draggableTextValue.setLowerLimit(0);
        draggableTextValue.setLowerLimitExists(true);
        draggableTextValue.setStep(0.01);
        draggableTextValue.setValue(getItemView().getScaleX());

        ChangeListener<? super Number> scaleListener = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });
        getItemView().scaleXProperty().addListener(scaleListener);
        return new HBox(draggableTextValue);
    }
    
    private Node createRotateValueNode(TemporalMetadata metadata){

        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                double step=newValue-oldValue;
                ShapeViewController.this.rotateBy(step);
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));
                //update the outline
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();
                //revise the value if it goes beyond 360
                if(newValue<0||newValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(newValue));
                }
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0||finalValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(finalValue));
                }
                RotateShape rotateShape=new RotateShape(ShapeViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(rotateShape, !dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        draggableTextValue.setStep(1);
        draggableTextValue.setValue(getItemView().getOriginRotate());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> rotationListener = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });

        getItemView().rotateProperty().addListener(rotationListener);
        return new HBox(draggableTextValue);
    }

    @Override
    public double scaleBy(double dScale) {

        //change scale component of the view and gizmo
        double oldScale = getItemView().getScaleX();
        double newScale=dScale+ oldScale;
        if(newScale<0.1){
            return getItemView().getScaleX();
        }
        getItemView().setScale(newScale);
        getGizmo().updateView();

        //TODO convert to work point scale and update the business model
        double workScale=newScale;
        getItemModel().setScale(workScale);
        return getScale();
    }

    @Override
    public double rotateBy(double dAngle) {//TODO make it absolute
        //change rotation component of the view and gizmo
        double oldRotation= getItemView().getOriginRotate();
        double newRotation=dAngle+ oldRotation;
        newRotation= MathUtil.under360(newRotation);
        getItemView().setOriginRotate(newRotation);
        getGizmo().updateView();

        //TODO convert to work point scale and update the business model
        double workRotation=newRotation;
        getItemModel().setRotation(workRotation);
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
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        switch (tag){

            case SCALE:
                return (TemporalMetadata)metadataTree.getChildren().get(SCALE_INDEX).getValue();
            case ROTATION:
                return (TemporalMetadata)metadataTree.getChildren().get(ROTATION_INDEX).getValue();
            default:
                return null;
        }
    }

    @Override
    public SpatialMetadata getSpatialMetadata(MetadataTag tag) {
        switch (tag){
            case TRANSLATION:
                return (SpatialMetadata)metadataTree.getChildren().get(TRANSLATION_INDEX).getValue();
            case ANCHOR_POINT:
                return (SpatialMetadata)metadataTree.getChildren().get(ANCHOR_POINT_INDEX).getValue();
            default:
                return null;
        }
    }

    @Override
    public void valueChanged(SpatialKeyframeChangeNode changeNode) {
        ShapeModel shapeModel = getItemModel();
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
        ShapeModel shapeModel = getItemModel();
        ShapeView shapeView = getItemView();
        if(changeNode==shapeModel.scaleChange()){
            shapeView.setScale(changeNode.getCurrentValue().get(0));// for all shapes, scale y is bind to scale x
        }else if(changeNode==shapeModel.rotationChange()){
            shapeView.setOriginRotate(changeNode.getCurrentValue().get(0));
        }
    }
}
