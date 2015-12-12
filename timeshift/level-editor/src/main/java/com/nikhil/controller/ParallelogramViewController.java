package com.nikhil.controller;


import com.nikhil.command.item.parallelogram.ChangeHeight;
import com.nikhil.command.item.parallelogram.ChangeSwayAngle;
import com.nikhil.command.item.parallelogram.ChangeWidth;
import com.nikhil.controller.item.ParallelogramModelController;

import com.nikhil.editor.gizmo.ParallelogramGizmo;
import com.nikhil.math.MathUtil;

import com.nikhil.model.shape.ParallelogramModel;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;

import com.nikhil.view.item.ParallelogramView;
import com.nikhil.view.item.delegate.ParallelogramViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

/**
 * View controller for the parallelogram model
 * Created by NikhilVerma on 06/12/15.
 */
public class ParallelogramViewController extends ShapeViewController implements ParallelogramViewDelegate {

    public static final int WIDTH_INDEX =4;
    public static final int HEIGHT_INDEX =5;
    public static final int SWAY_ANGLE_INDEX =6;

    //TODO dynamic limits on draggable text values with rejection of invalid values
    private ParallelogramModelController parallelogramModelController;
    private ParallelogramView parallelogramView;
    private ParallelogramGizmo parallelogramGizmo;

    public ParallelogramViewController(ParallelogramViewController parallelogramViewController) {
        this(parallelogramViewController.compositionViewController,new ParallelogramView(parallelogramViewController.parallelogramView));
    }

    public ParallelogramViewController(CompositionViewController compositionViewController,ParallelogramView parallelogramView) {
        super(compositionViewController);
        this.parallelogramView=parallelogramView;
        this.parallelogramView.setDelegate(this);
        constructModelControllerUsingView();
        parallelogramGizmo=new ParallelogramGizmo(parallelogramView);
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this, false);
    }

    public ParallelogramViewController(CompositionViewController compositionViewController,ParallelogramModelController parallelogramModelController){
        super(compositionViewController);
        this.parallelogramModelController=parallelogramModelController;
        constructViewUsingModelController();
        this.parallelogramView.setDelegate(this);
        parallelogramGizmo=new ParallelogramGizmo(parallelogramView);
        setSelfAsChangeHandler();
        initMetadataTree();
    }

    private void constructViewUsingModelController() {

        ParallelogramModel parallelogramModel=parallelogramModelController.getParallelogramModel();

        //get the simple properties from the model
        double scale=parallelogramModel.getScale();
        double rotation=parallelogramModel.getRotation();
        UtilPoint translation=parallelogramModel.getTranslation();
//        UtilPoint anchorPoint=parallelogramModel.getAnchorPoint();//TODO anchor point not currently being developed

        this.parallelogramView=new ParallelogramView(parallelogramModel.getWidth(),parallelogramModel.getHeight(),
                parallelogramModel.getSwayAngle());
        this.parallelogramView.setScale(scale);
        this.parallelogramView.setRotate(rotation);
        this.parallelogramView.setLayoutX(translation.getX());
        this.parallelogramView.setLayoutY(translation.getY());
    }

    @Override
    public ParallelogramModelController getModelController() {
        return parallelogramModelController;
    }

    @Override
    public ParallelogramViewController deepCopy() {
        return new ParallelogramViewController(this);
    }

    @Override
    public ParallelogramView getItemView() {
        return parallelogramView;
    }

    @Override
    public ParallelogramGizmo getGizmo() {
        return parallelogramGizmo;
    }

    @Override
    public ParallelogramModel getItemModel() {
        return parallelogramModelController.getParallelogramModel();
    }

    @Override
    protected void constructModelControllerUsingView() {
        ParallelogramModel parallelogramModel=new ParallelogramModel(parallelogramView.getWidth(),parallelogramView.getHeight(),
                parallelogramView.getSwayAngle());
        parallelogramModel.setScale(parallelogramView.getScale());
        parallelogramModel.setRotation(parallelogramView.getOriginRotate());
        parallelogramModel.setTranslation(parallelogramView.getLayoutX(),parallelogramView.getLayoutY());
        //TODO anchor point
        parallelogramModelController=new ParallelogramModelController(parallelogramModel);
    }

    @Override
    public void initMetadataTree() {
        super.initMetadataTree();

        //Width
        final TemporalMetadata widthMeta = new TemporalMetadata(MetadataTag.PARALLELOGRAM_WIDTH, parallelogramModelController.getParallelogramModel().widthChange(), this);
        widthMeta.setValueNode(createWidthValueNode(widthMeta));
        TreeItem<Metadata> widthTreeItem= new TreeItem<>(widthMeta);

        //Height
        final TemporalMetadata heightMeta = new TemporalMetadata(MetadataTag.PARALLELOGRAM_HEIGHT, parallelogramModelController.getParallelogramModel().heightChange(), this);
        heightMeta.setValueNode(createHeightValueNode(heightMeta));
        TreeItem<Metadata> heightTreeItem= new TreeItem<>(heightMeta);

        //SwayAngle
        final TemporalMetadata swayAngleMeta = new TemporalMetadata(MetadataTag.PARALLELOGRAM_SWAY_ANGLE, parallelogramModelController.getParallelogramModel().swayAngleChange(), this);
        swayAngleMeta.setValueNode(createSwayAngleValueNode(swayAngleMeta));
        TreeItem<Metadata> swayAngleTreeItem= new TreeItem<>(swayAngleMeta);

        metadataTree.getChildren().add(WIDTH_INDEX,widthTreeItem);
        metadataTree.getChildren().add(HEIGHT_INDEX,heightTreeItem);
        metadataTree.getChildren().add(SWAY_ANGLE_INDEX,swayAngleTreeItem);


    }

    private Node createWidthValueNode(TemporalMetadata metadata){

        DraggableTextValue widthDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                parallelogramView.setWidth(newValue);
                getItemModel().setWidth(newValue);
                //update the gizmo and the outline
                parallelogramGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }
                getItemModel().setWidth(finalValue);
                ChangeWidth changeWidth=new ChangeWidth(ParallelogramViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeWidth,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        widthDragger.setStep(1);
        widthDragger.setLowerLimitExists(true);
        widthDragger.setLowerLimit(0);
        widthDragger.setValue(getItemView().getWidth());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> widthListener = ((observable, oldValue, newValue) -> {
            widthDragger.setValue(newValue.doubleValue());
        });

        getItemView().widthProperty().addListener(widthListener);
        return new HBox(widthDragger);
    }

    private Node createHeightValueNode(TemporalMetadata metadata){

        DraggableTextValue heightDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                parallelogramView.setHeight(newValue);
                getItemModel().setHeight(newValue);
                //update the gizmo and the outline
                parallelogramGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }
                getItemModel().setHeight(finalValue);

                ChangeHeight changeHeight=new ChangeHeight(ParallelogramViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeHeight,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        heightDragger.setStep(1);
        heightDragger.setLowerLimitExists(true);
        heightDragger.setLowerLimit(0);
        heightDragger.setValue(getItemView().getHeight());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> heightListener = ((observable, oldValue, newValue) -> {
            heightDragger.setValue(newValue.doubleValue());
        });

        getItemView().heightProperty().addListener(heightListener);
        return new HBox(heightDragger);
    }

    private Node createSwayAngleValueNode(TemporalMetadata metadata){

        DraggableTextValue swayAngleDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                parallelogramView.setSwayAngle(newValue);
                getItemModel().setSwayAngle(newValue);
                //update the gizmo and the outline
                parallelogramGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0||finalValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(finalValue));
                }
                getItemModel().setSwayAngle(finalValue);
                ChangeSwayAngle changeSwayAngle=new ChangeSwayAngle(ParallelogramViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeSwayAngle,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        swayAngleDragger.setStep(1);
        swayAngleDragger.setLowerLimitExists(true);
        swayAngleDragger.setLowerLimit(0);
        swayAngleDragger.setUpperLimitExists(true);
        swayAngleDragger.setUpperLimit(360);
        swayAngleDragger.setValue(getItemView().getSwayAngle());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> swayAngleListener = ((observable, oldValue, newValue) -> {
            swayAngleDragger.setValue(newValue.doubleValue());
        });

        getItemView().swayAngleProperty().addListener(swayAngleListener);
        return new HBox(swayAngleDragger);
    }

    @Override
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        if (tag == MetadataTag.PARALLELOGRAM_WIDTH) {
            return (TemporalMetadata) metadataTree.getChildren().get(WIDTH_INDEX).getValue();
        } else if (tag == MetadataTag.PARALLELOGRAM_HEIGHT) {
            return (TemporalMetadata) metadataTree.getChildren().get(HEIGHT_INDEX).getValue();
        } else if (tag == MetadataTag.PARALLELOGRAM_SWAY_ANGLE) {
            return (TemporalMetadata) metadataTree.getChildren().get(SWAY_ANGLE_INDEX).getValue();
        } else {
            return super.getTemporalMetadata(tag);
        }
    }


    @Override
    public void tweakingWidth(double oldWidth, double newWidth) {
        getItemModel().setWidth(newWidth);
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_WIDTH).registerContinuousChange(new KeyValue(oldWidth),new KeyValue(newWidth));
    }

    @Override
    public void tweakingHeight(double oldHeight, double newHeight) {
        getItemModel().setHeight(newHeight);
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_HEIGHT).registerContinuousChange(new KeyValue(oldHeight),new KeyValue(newHeight));
    }

    @Override
    public void tweakingSwayAngle(double oldSwayAngle, double newSwayAngle) {
        getItemModel().setSwayAngle(newSwayAngle);
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_SWAY_ANGLE).registerContinuousChange(new KeyValue(oldSwayAngle),new KeyValue(newSwayAngle));
    }

    @Override
    public void finishedTweakingWidth(double initialWidth) {
        ChangeWidth changeWidth=new ChangeWidth(this,initialWidth,parallelogramView.getWidth());
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_WIDTH).pushWithKeyframe(changeWidth,false);
    }

    @Override
    public void finishedTweakingHeight(double initialHeight) {
        ChangeHeight changeHeight=new ChangeHeight(this,initialHeight,parallelogramView.getHeight());
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_HEIGHT).pushWithKeyframe(changeHeight, false);
    }

    @Override
    public void finishedTweakingSwayAngle(double initialSwayAngle) {
        ChangeSwayAngle changeSwayAngle=new ChangeSwayAngle(this,initialSwayAngle,parallelogramView.getSwayAngle());
        getTemporalMetadata(MetadataTag.PARALLELOGRAM_SWAY_ANGLE).pushWithKeyframe(changeSwayAngle, false);
    }

    protected void setSelfAsChangeHandler(){
        //call super to mind the common shape properties
        super.setSelfAsChangeHandler();

        //set self as change handler for all the specific properties
        ParallelogramModel parallelogramModel = getItemModel();
        parallelogramModel.widthChange().setChangeHandler(this);
        parallelogramModel.heightChange().setChangeHandler(this);
        parallelogramModel.swayAngleChange().setChangeHandler(this);
    }

    @Override
    public void valueChanged(TemporalKeyframeChangeNode changeNode) {
        //call super to mind any change in the common shape properties
        super.valueChanged(changeNode);

        //if its either of the parallelogram's properties , handle it here
        ParallelogramModel parallelogramModel = getItemModel();
        if(changeNode==parallelogramModel.widthChange()){
            parallelogramView.setWidth(changeNode.getCurrentValue().get(0));
        }else if(changeNode==parallelogramModel.heightChange()){
            parallelogramView.setHeight(changeNode.getCurrentValue().get(0));
        }else if(changeNode==parallelogramModel.swayAngleChange()){
            parallelogramView.setSwayAngle(changeNode.getCurrentValue().get(0));
        }
        parallelogramGizmo.updateView();
    }
}

