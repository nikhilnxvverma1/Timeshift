package com.nikhil.controller;

import com.nikhil.command.item.RotateShape;
import com.nikhil.command.item.circle.ChangeEndAngle;
import com.nikhil.command.item.circle.ChangeInnerRadius;
import com.nikhil.command.item.circle.ChangeOuterRadius;
import com.nikhil.command.item.circle.ChangeStartAngle;
import com.nikhil.controller.item.CircleModelController;
import com.nikhil.controller.item.ItemModelController;
import com.nikhil.editor.gizmo.CircleGizmo;
import com.nikhil.logging.Logger;
import com.nikhil.math.MathUtil;
import com.nikhil.model.shape.CircleModel;
import com.nikhil.model.shape.ShapeModel;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.item.CircleView;
import com.nikhil.view.item.delegate.CircleViewDelegate;
import com.nikhil.view.item.record.Metadata;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.PolygonMetadata;
import com.nikhil.view.item.record.TemporalMetadata;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Shape;

/**
 * View controller for the circle model
 * Created by NikhilVerma on 06/12/15.
 */
public class CircleViewController extends ShapeViewController implements CircleViewDelegate{

    public static final int INNER_RADIUS_INDEX=4;
    public static final int OUTER_RADIUS_INDEX=5;
    public static final int START_ANGLE_INDEX=6;
    public static final int END_ANGLE_INDEX=7;
    
    //TODO dynamic limits on draggable text values with rejection of invalid values
    private CircleModelController circleModelController;
    private CircleView circleView;
    private CircleGizmo circleGizmo;

    public CircleViewController(CircleViewController circleViewController) {
        this(circleViewController.compositionViewController,new CircleView(circleViewController.circleView));
    }

    public CircleViewController(CompositionViewController compositionViewController,CircleView circleView) {
        super(compositionViewController);
        this.circleView=circleView;
        this.circleView.setDelegate(this);
        constructModelControllerUsingView();
        circleGizmo=new CircleGizmo(circleView);
        setSelfAsChangeHandler();
        initMetadataTree();
        compositionViewController.getWorkspace().getSelectedItems().requestFocus(this, false);
    }

    public CircleViewController(CompositionViewController compositionViewController,CircleModelController circleModelController){
        super(compositionViewController);
        this.circleModelController=circleModelController;
        constructViewUsingModelController();
        this.circleView.setDelegate(this);
        circleGizmo=new CircleGizmo(circleView);
        setSelfAsChangeHandler();
        initMetadataTree();
    }

    private void constructViewUsingModelController() {

        CircleModel circleModel=circleModelController.getCircleModel();

        //get the simple properties from the model
        double scale=circleModel.getScale();
        double rotation=circleModel.getRotation();
        UtilPoint translation=circleModel.getTranslation();
//        UtilPoint anchorPoint=circleModel.getAnchorPoint();//TODO anchor point not currently being developed

        this.circleView=new CircleView(circleModel.getInnerRadius(),circleModel.getOuterRadius(),
                circleModel.getStartAngle(),circleModel.getEndAngle());
        this.circleView.setScale(scale);
        this.circleView.setRotate(rotation);
        this.circleView.setLayoutX(translation.getX());
        this.circleView.setLayoutY(translation.getY());
    }

    @Override
    public CircleModelController getModelController() {
        return circleModelController;
    }

    @Override
    public CircleViewController deepCopy() {
        return new CircleViewController(this);
    }

    @Override
    public CircleView getItemView() {
        return circleView;
    }

    @Override
    public CircleGizmo getGizmo() {
        return circleGizmo;
    }

    @Override
    public CircleModel getItemModel() {
        return circleModelController.getCircleModel();
    }

    @Override
    protected void constructModelControllerUsingView() {
        CircleModel circleModel=new CircleModel(circleView.getInnerRadius(),circleView.getOuterRadius(),
                circleView.getStartAngle(),circleView.getEndAngle());
        circleModelController=new CircleModelController(circleModel);
    }

    @Override
    public void initMetadataTree() {
        super.initMetadataTree();
        
        //InnerRadius
        final TemporalMetadata innerRadiusMeta = new TemporalMetadata(MetadataTag.INNER_RADIUS, circleModelController.getCircleModel().innerRadiusChange(), this);
        innerRadiusMeta.setValueNode(createInnerRadiusValueNode(innerRadiusMeta));
        TreeItem<Metadata> innerRadiusTreeItem= new TreeItem<>(innerRadiusMeta);

        //OuterRadius
        final TemporalMetadata outerRadiusMeta = new TemporalMetadata(MetadataTag.OUTER_RADIUS, circleModelController.getCircleModel().outerRadiusChange(), this);
        outerRadiusMeta.setValueNode(createOuterRadiusValueNode(outerRadiusMeta));
        TreeItem<Metadata> outerRadiusTreeItem= new TreeItem<>(outerRadiusMeta);

        //StartAngle
        final TemporalMetadata startAngleMeta = new TemporalMetadata(MetadataTag.START_ANGLE, circleModelController.getCircleModel().startAngleChange(), this);
        startAngleMeta.setValueNode(createStartAngleValueNode(startAngleMeta));
        TreeItem<Metadata> startAngleTreeItem= new TreeItem<>(startAngleMeta);

        //EndAngle
        final TemporalMetadata endAngleMeta = new TemporalMetadata(MetadataTag.END_ANGLE, circleModelController.getCircleModel().endAngleChange(), this);
        endAngleMeta.setValueNode(createEndAngleValueNode(endAngleMeta));
        TreeItem<Metadata> endAngleTreeItem= new TreeItem<>(endAngleMeta);
        
        metadataTree.getChildren().add(INNER_RADIUS_INDEX,innerRadiusTreeItem);
        metadataTree.getChildren().add(OUTER_RADIUS_INDEX,outerRadiusTreeItem);
        metadataTree.getChildren().add(START_ANGLE_INDEX,startAngleTreeItem);
        metadataTree.getChildren().add(END_ANGLE_INDEX,endAngleTreeItem);

        
    }
    
    private Node createInnerRadiusValueNode(TemporalMetadata metadata){

        DraggableTextValue innerRadiusDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                
                circleView.setInnerRadius(newValue);
                //update the gizmo and the outline
                circleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();
                
                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));
                
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }
                if(finalValue>=circleView.getOuterRadius()){
                    draggableTextValue.setValue(circleView.getOuterRadius()-1);
                }
                ChangeInnerRadius changeInnerRadius=new ChangeInnerRadius(CircleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeInnerRadius,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        innerRadiusDragger.setStep(1);
        innerRadiusDragger.setLowerLimitExists(true);
        innerRadiusDragger.setLowerLimit(0);
        innerRadiusDragger.setValue(getItemView().getInnerRadius());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> innerRadiusListener = ((observable, oldValue, newValue) -> {
            innerRadiusDragger.setValue(newValue.doubleValue());
        });

        getItemView().innerRadiusProperty().addListener(innerRadiusListener);
        return new HBox(innerRadiusDragger);
    }

    private Node createOuterRadiusValueNode(TemporalMetadata metadata){

        DraggableTextValue outerRadiusDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                circleView.setOuterRadius(newValue);
                //update the gizmo and the outline
                circleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0){
                    draggableTextValue.setValue(0);
                }
                if(finalValue<=circleView.getInnerRadius()){
                    draggableTextValue.setValue(circleView.getInnerRadius()+1);
                }
                ChangeOuterRadius changeOuterRadius=new ChangeOuterRadius(CircleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeOuterRadius,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        outerRadiusDragger.setStep(1);
        outerRadiusDragger.setLowerLimitExists(true);
        outerRadiusDragger.setLowerLimit(0);
        outerRadiusDragger.setValue(getItemView().getOuterRadius());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> outerRadiusListener = ((observable, oldValue, newValue) -> {
            outerRadiusDragger.setValue(newValue.doubleValue());
        });

        getItemView().outerRadiusProperty().addListener(outerRadiusListener);
        return new HBox(outerRadiusDragger);
    }

    private Node createStartAngleValueNode(TemporalMetadata metadata){

        DraggableTextValue startAngleDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                circleView.setStartAngle(newValue);
                //update the gizmo and the outline
                circleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0||finalValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(finalValue));
                }
                if(finalValue>=circleView.getEndAngle()){
                    draggableTextValue.setValue(circleView.getEndAngle()-1);
                }
                ChangeStartAngle changeStartAngle=new ChangeStartAngle(CircleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeStartAngle,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        startAngleDragger.setStep(1);
        startAngleDragger.setLowerLimitExists(true);
        startAngleDragger.setLowerLimit(0);
        startAngleDragger.setUpperLimitExists(true);
        startAngleDragger.setUpperLimit(360);
        startAngleDragger.setValue(getItemView().getStartAngle());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> startAngleListener = ((observable, oldValue, newValue) -> {
            startAngleDragger.setValue(newValue.doubleValue());
        });

        getItemView().startAngleProperty().addListener(startAngleListener);
        return new HBox(startAngleDragger);
    }

    private Node createEndAngleValueNode(TemporalMetadata metadata){

        DraggableTextValue endAngleDragger=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                circleView.setEndAngle(newValue);
                //update the gizmo and the outline
                circleGizmo.updateView();
                getCompositionViewController().getWorkspace().getSelectedItems().updateView();

                //register this change to the metadata object
                metadata.registerContinuousChange(new KeyValue(oldValue), new KeyValue(newValue));

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                if(finalValue<0||finalValue>=360){
                    draggableTextValue.setValue(MathUtil.under360(finalValue));
                }
                if(finalValue<=circleView.getStartAngle()){
                    draggableTextValue.setValue(circleView.getStartAngle()+1);
                }
                ChangeEndAngle changeEndAngle=new ChangeEndAngle(CircleViewController.this,initialValue,finalValue);
                metadata.pushWithKeyframe(changeEndAngle,!dragged);
            }

            @Override
            public boolean isEnabled() {
                return isInteractive();
            }
        });
        endAngleDragger.setStep(1);
        endAngleDragger.setLowerLimitExists(true);
        endAngleDragger.setLowerLimit(0);
        endAngleDragger.setUpperLimitExists(true);
        endAngleDragger.setUpperLimit(359.99);
        endAngleDragger.setValue(getItemView().getEndAngle());

        //TODO save as field so as to release memory later?
        ChangeListener<? super Number> endAngleListener = ((observable, oldValue, newValue) -> {
            endAngleDragger.setValue(newValue.doubleValue());
        });

        getItemView().endAngleProperty().addListener(endAngleListener);
        return new HBox(endAngleDragger);
    }

    @Override
    public TemporalMetadata getTemporalMetadata(MetadataTag tag) {
        if (tag == MetadataTag.INNER_RADIUS) {
            return (TemporalMetadata) metadataTree.getChildren().get(INNER_RADIUS_INDEX).getValue();
        } else if (tag == MetadataTag.OUTER_RADIUS) {
            return (TemporalMetadata) metadataTree.getChildren().get(OUTER_RADIUS_INDEX).getValue();
        } else if (tag == MetadataTag.START_ANGLE) {
            return (TemporalMetadata) metadataTree.getChildren().get(START_ANGLE_INDEX).getValue();
        } else if (tag == MetadataTag.END_ANGLE) {
            return (TemporalMetadata) metadataTree.getChildren().get(END_ANGLE_INDEX).getValue();
        } else {
            return super.getTemporalMetadata(tag);
        }
    }


    @Override
    public void tweakingInnerRadius(double oldInnerRadius, double newInnerRadius) {
        getTemporalMetadata(MetadataTag.INNER_RADIUS).registerContinuousChange(new KeyValue(oldInnerRadius),new KeyValue(newInnerRadius));
    }

    @Override
    public void tweakingOuterRadius(double oldOuterRadius, double newOuterRadius) {
        getTemporalMetadata(MetadataTag.OUTER_RADIUS).registerContinuousChange(new KeyValue(oldOuterRadius),new KeyValue(newOuterRadius));
    }

    @Override
    public void tweakingStartAngle(double oldStartAngle, double newStartAngle) {
        getTemporalMetadata(MetadataTag.START_ANGLE).registerContinuousChange(new KeyValue(oldStartAngle),new KeyValue(newStartAngle));
    }

    @Override
    public void tweakingEndAngle(double oldEndAngle, double newEndAngle) {
        getTemporalMetadata(MetadataTag.END_ANGLE).registerContinuousChange(new KeyValue(oldEndAngle),new KeyValue(newEndAngle));
    }

    @Override
    public void finishedTweakingInnerRadius(double initialInnerRadius) {
        ChangeInnerRadius changeInnerRadius=new ChangeInnerRadius(this,initialInnerRadius,circleView.getInnerRadius());
        getTemporalMetadata(MetadataTag.INNER_RADIUS).pushWithKeyframe(changeInnerRadius,false);
    }

    @Override
    public void finishedTweakingOuterRadius(double initialOuterRadius) {
        ChangeOuterRadius changeOuterRadius=new ChangeOuterRadius(this,initialOuterRadius,circleView.getOuterRadius());
        getTemporalMetadata(MetadataTag.OUTER_RADIUS).pushWithKeyframe(changeOuterRadius, false);
    }

    @Override
    public void finishedTweakingStartAngle(double initialStartAngle) {
        ChangeStartAngle changeStartAngle=new ChangeStartAngle(this,initialStartAngle,circleView.getStartAngle());
        getTemporalMetadata(MetadataTag.START_ANGLE).pushWithKeyframe(changeStartAngle, false);
    }

    @Override
    public void finishedTweakingEndAngle(double initialEndAngle) {
        ChangeEndAngle changeEndAngle=new ChangeEndAngle(this,initialEndAngle,circleView.getEndAngle());
        getTemporalMetadata(MetadataTag.END_ANGLE).pushWithKeyframe(changeEndAngle, false);
    }

    protected void setSelfAsChangeHandler(){
        //call super to mind the common shape properties
        super.setSelfAsChangeHandler();

        //set self as change handler for all the specific properties
        CircleModel circleModel = getItemModel();
        circleModel.innerRadiusChange().setChangeHandler(this);
        circleModel.outerRadiusChange().setChangeHandler(this);
        circleModel.startAngleChange().setChangeHandler(this);
        circleModel.endAngleChange().setChangeHandler(this);
    }

    @Override
    public void valueChanged(TemporalKeyframeChangeNode changeNode) {
        //call super to mind any change in the common shape properties
        super.valueChanged(changeNode);

        //if its either of the circle's properties , handle it here
        CircleModel circleModel = getItemModel();
        if(changeNode==circleModel.innerRadiusChange()){
            circleView.setInnerRadius(changeNode.getCurrentValue().get(0));
        }else if(changeNode==circleModel.outerRadiusChange()){
            circleView.setOuterRadius(changeNode.getCurrentValue().get(0));
        }else if(changeNode==circleModel.startAngleChange()){
            circleView.setStartAngle(changeNode.getCurrentValue().get(0));
        }else if(changeNode==circleModel.endAngleChange()){
            circleView.setEndAngle(changeNode.getCurrentValue().get(0));
        }
        circleGizmo.updateView();
    }
}
