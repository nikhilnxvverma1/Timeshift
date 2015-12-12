package com.nikhil.controller;

import com.nikhil.command.item.triangle.ChangeBase;
import com.nikhil.command.item.triangle.ChangeHeight;
import com.nikhil.controller.item.TriangleModelController;
import com.nikhil.editor.gizmo.TriangleGizmo;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.model.shape.TriangleModel;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.item.TriangleView;
import com.nikhil.view.item.delegate.TriangleViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

/**
 * View controller for the Triangle model
 * Created by NikhilVerma on 11/12/15.
 */
public class TriangleViewController extends ShapeViewController implements TriangleViewDelegate{

    public static final int BASE_INDEX=4;
    public static final int HEIGHT_INDEX=5;

    private TriangleView triangleView;
    private TriangleGizmo triangleGizmo;
    private TriangleModelController triangleModelController;

    public TriangleViewController(TriangleViewController triangleViewController){
        this(triangleViewController.compositionViewController,new TriangleView(triangleViewController.triangleView));
    }

    public TriangleViewController(CompositionViewController compositionViewController, TriangleView triangleView) {
        super(compositionViewController);

        this.triangleView=triangleView;
        this.triangleView.setDelegate(this);
        constructModelControllerUsingView();
        triangleGizmo=new TriangleGizmo(triangleView);
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this, false);
    }

    public TriangleViewController(CompositionViewController compositionViewController, TriangleModelController triangleModelController) {
        super(compositionViewController);
        this.triangleModelController=triangleModelController;
        constructViewUsingModelController();
        this.triangleView.setDelegate(this);
        triangleGizmo=new TriangleGizmo(triangleView);
        setSelfAsChangeHandler();
        initMetadataTree();
    }

    @Override
    protected void constructModelControllerUsingView() {
        TriangleModel triangleModel=new TriangleModel(triangleView.getBase(),triangleView.getHeight());
        triangleModel.setScale(triangleView.getScale());
        triangleModel.setRotation(triangleView.getOriginRotate());
        Workspace workspace = compositionViewController.getWorkspace();
        double modelX = workspace.workPointX(triangleView.getLayoutX());
        double modelY = workspace.workPointY(triangleView.getLayoutY());
        triangleModel.setTranslation(modelX, modelY);
        //TODO anchor point
        triangleModelController=new TriangleModelController(triangleModel);
    }

    private void constructViewUsingModelController() {

        TriangleModel triangleModel=triangleModelController.getTriangleModel();

        //get the simple properties from the model
        double scale=triangleModel.getScale();
        double rotation=triangleModel.getRotation();
        UtilPoint translation=triangleModel.getTranslation();
//        UtilPoint anchorPoint=triangleModel.getAnchorPoint();//TODO anchor point not currently being developed

        this.triangleView=new TriangleView(triangleModel.getBase(),triangleModel.getHeight());
        this.triangleView.setScale(scale);
        this.triangleView.setRotate(rotation);
        this.triangleView.setLayoutX(translation.getX());
        this.triangleView.setLayoutY(translation.getY());
    }


    @Override
    protected void initMetadataTree() {
        super.initMetadataTree();

        //Base
        final TemporalMetadata baseMeta = new TemporalMetadata(MetadataTag.BASE, triangleModelController.getTriangleModel().baseChange(), this);
        baseMeta.setValueNode(createBaseValueNode(baseMeta));
        TreeItem<Metadata> baseTreeItem= new TreeItem<>(baseMeta);

        //Height
        final TemporalMetadata heightMeta = new TemporalMetadata(MetadataTag.HEIGHT, triangleModelController.getTriangleModel().heightChange(), this);
        heightMeta.setValueNode(createHeightValueNode(heightMeta));
        TreeItem<Metadata> heightTreeItem= new TreeItem<>(heightMeta);

        metadataTree.getChildren().add(BASE_INDEX,baseTreeItem);
        metadataTree.getChildren().add(HEIGHT_INDEX,heightTreeItem);
        
    }

    private Node createHeightValueNode(TemporalMetadata metadata) {
        DraggableTextValue innerRadiusDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                triangleView.setHeight(newValue);
                //update the gizmo and the outline
                triangleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }

                ChangeHeight changeHeight=new ChangeHeight(TriangleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeHeight,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        innerRadiusDragger.setStep(1);
        innerRadiusDragger.setLowerLimitExists(true);
        innerRadiusDragger.setLowerLimit(0);
        innerRadiusDragger.setValue(getItemView().getHeight());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> heightListener = ((observable, oldValue, newValue) -> {
            innerRadiusDragger.setValue(newValue.doubleValue());
        });

        getItemView().heightProperty().addListener(heightListener);
        return new HBox(innerRadiusDragger);
    }

    private Node createBaseValueNode(TemporalMetadata metadata) {
        DraggableTextValue innerRadiusDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                triangleView.setBase(newValue);
                //update the gizmo and the outline
                triangleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }

                ChangeBase changeBase=new ChangeBase(TriangleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeBase,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        innerRadiusDragger.setStep(1);
        innerRadiusDragger.setLowerLimitExists(true);
        innerRadiusDragger.setLowerLimit(0);
        innerRadiusDragger.setValue(getItemView().getBase());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> baseListener = ((observable, oldValue, newValue) -> {
            innerRadiusDragger.setValue(newValue.doubleValue());
        });

        getItemView().baseProperty().addListener(baseListener);
        return new HBox(innerRadiusDragger);
    }

    @Override
    public TriangleModel getItemModel() {
        return triangleModelController.getTriangleModel();
    }

    @Override
    public TriangleGizmo getGizmo() {
        return triangleGizmo;
    }

    @Override
    public TriangleModelController getModelController() {
        return triangleModelController;
    }

    @Override
    public TriangleViewController deepCopy() {
        return new TriangleViewController(this);
    }

    @Override
    public TriangleView getItemView() {
        return triangleView;
    }

    @Override
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        if (tag == MetadataTag.BASE) {
            return (TemporalMetadata) metadataTree.getChildren().get(BASE_INDEX).getValue();
        } else if (tag == MetadataTag.HEIGHT) {
            return (TemporalMetadata) metadataTree.getChildren().get(HEIGHT_INDEX).getValue();
        } else {
            return super.getTemporalMetadata(tag);
        }
    }

    @Override
    public void tweakingBase(double oldBase, double newBase) {
        getItemModel().setBase(newBase);
        getTemporalMetadata(MetadataTag.BASE).registerContinuousChange(new KeyValue(oldBase),new KeyValue(newBase));
    }

    @Override
    public void tweakingHeight(double oldHeight, double newHeight) {
        getItemModel().setHeight(newHeight);
        getTemporalMetadata(MetadataTag.HEIGHT).registerContinuousChange(new KeyValue(oldHeight),new KeyValue(newHeight));
    }

    @Override
    public void finishedTweakingBase(double initialBase) {
        ChangeBase changeBase=new ChangeBase(this,initialBase,triangleView.getBase());
        getTemporalMetadata(MetadataTag.BASE).pushWithKeyframe(changeBase,false);
    }

    @Override
    public void finishedTweakingHeight(double initialHeight) {
        ChangeHeight changeHeight=new ChangeHeight(this,initialHeight,triangleView.getHeight());
        getTemporalMetadata(MetadataTag.HEIGHT).pushWithKeyframe(changeHeight,false);
    }

    protected void setSelfAsChangeHandler(){
        //call super to mind the common shape properties
        super.setSelfAsChangeHandler();

        //set self as change handler for all the specific properties
        TriangleModel triangleModel = getItemModel();
        triangleModel.baseChange().setChangeHandler(this);
        triangleModel.heightChange().setChangeHandler(this);

    }

    @Override
    public void valueChanged(TemporalKeyframeChangeNode changeNode) {
        //call super to mind any change in the common shape properties
        super.valueChanged(changeNode);

        //if its either of the triangle's properties , handle it here
        TriangleModel triangleModel = getItemModel();
        if(changeNode==triangleModel.baseChange()){
            triangleView.setBase(changeNode.getCurrentValue().get(0));
        }else if(changeNode==triangleModel.heightChange()){
            triangleView.setHeight(changeNode.getCurrentValue().get(0));
        }
        triangleGizmo.updateView();
    }
}
