package com.nikhil.view.item.record;

import com.nikhil.command.MoveItemSet;
import com.nikhil.command.RotateShape;
import com.nikhil.command.ScaleShape;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.PolygonViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.math.MathUtil;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.Set;

/**
 * TODO Each draggable text field registers a listener on the polygon view. This might leak memory
 * because the table cells are getting recycled(they are virtualized).
 * Created by NikhilVerma on 13/10/15.
 */
public class PolygonMetadata extends Metadata{
    private PolygonViewController polygonViewController;
    private ChangeListener<? super Number>[]propertyListeners=null;//hold reference to prevent it from being garbage collected

    public PolygonMetadata(String name, int tag, PolygonViewController polygonViewController) {
        super(name, tag);
        this.polygonViewController = polygonViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return polygonViewController;
    }


    @Override
    public Node getValueNode() {
        switch (tag){
            case PolygonViewController.HEADER_TAG:
                return new Button("Reset");//TODO delegation and visual size
            case PolygonViewController.SCALE_TAG:
                return getScaleValueNode();
            case PolygonViewController.ROTATION_TAG:
                return getRotationValueNode();
            case PolygonViewController.TRANSLATION_TAG:
                return getTranslationValueNode();
            case PolygonViewController.ANCHOR_POINT_TAG:
                return getAnchorPointValueNode();
            case PolygonViewController.VERTICES_TAG:
                return getVertexCountValueNode();
        }
        return null;
    }

    private KeyValue getKeyValue(){
        switch (tag){
            case PolygonViewController.SCALE_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getScale());
            case PolygonViewController.ROTATION_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getRotate());
            case PolygonViewController.TRANSLATION_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getLayoutX(),
                        polygonViewController.getPolygonView().getLayoutY());
            case PolygonViewController.ANCHOR_POINT_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getTranslateX(),
                        polygonViewController.getPolygonView().getTranslateY());
            case PolygonViewController.VERTICES_TAG:
                return new KeyValue(polygonViewController.getPolygonView().getPolygonPoints().size());
        }
        return null;
    }

    private HBox getScaleValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                polygonViewController.scaleBy(newValue - oldValue);
                //update the outline
                polygonViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                ScaleShape scaleShape =new ScaleShape(polygonViewController,initialValue,finalValue);
                polygonViewController.getCompositionViewController().getWorkspace().pushCommand(scaleShape,!dragged);
            }
        });
        draggableTextValue.setLowerLimit(0);
        draggableTextValue.setLowerLimitExists(true);
        draggableTextValue.setStep(0.01);
        draggableTextValue.setValue(polygonViewController.getPolygonView().getScale());
        if(propertyListeners !=null){
            polygonViewController.getPolygonView().scaleProperty().removeListener(propertyListeners[0]);
        }
        propertyListeners = new ChangeListener[1];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });
        polygonViewController.getPolygonView().scaleProperty().addListener(propertyListeners[0]);
        return new HBox(draggableTextValue);
    }

    private HBox getRotationValueNode(){
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                double step=newValue-oldValue;
                polygonViewController.rotateBy(step);
                //update the outline
                polygonViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
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
                RotateShape rotateShape=new RotateShape(polygonViewController,initialValue,finalValue);
                polygonViewController.getCompositionViewController().getWorkspace().pushCommand(rotateShape,!dragged);
            }
        });
        draggableTextValue.setStep(1);
        draggableTextValue.setValue(polygonViewController.getPolygonView().getRotate());

        if(propertyListeners !=null){
            polygonViewController.getPolygonView().rotateProperty().removeListener(propertyListeners[0]);
        }
        propertyListeners = new ChangeListener[1];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });

        polygonViewController.getPolygonView().rotateProperty().addListener(propertyListeners[0]);
        return new HBox(draggableTextValue);
    }

    private HBox getTranslationValueNode(){
        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                polygonViewController.moveBy(newValue-oldValue,0);
                //update the outline
                polygonViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

                //get the reference to the workspace
                Workspace workspace = polygonViewController.getCompositionViewController().getWorkspace();

                //get the reference to the selected items
                SelectedItems selectedItems = workspace.getSelectedItems();

                //create a new item set from the SelectedItems (uses old if it exists)
                Set<ItemViewController> itemSetForNewCommand = selectedItems.getItemSetForNewCommand();

                //create the initial and final points by supplying the same y across both points
                Point2D initialPoint=new Point2D(initialValue,polygonViewController.getTranslation().getY());
                Point2D finalPoint=new Point2D(finalValue,polygonViewController.getTranslation().getY());

                //push the move command for shifting horizontally,without executing it
                MoveItemSet shiftHorizontally=new MoveItemSet(itemSetForNewCommand,selectedItems,initialPoint,finalPoint);
                workspace.pushCommand(shiftHorizontally, !dragged);
            }
        });
        xValue.setStep(1);
        xValue.setValue(polygonViewController.getPolygonView().getLayoutX());

        if((propertyListeners !=null)&&(propertyListeners.length==2)){
            polygonViewController.getPolygonView().layoutXProperty().removeListener(propertyListeners[0]);
            polygonViewController.getPolygonView().layoutYProperty().removeListener(propertyListeners[1]);
        }
        propertyListeners = new ChangeListener[2];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            xValue.setValue(newValue.doubleValue());
        });

        polygonViewController.getPolygonView().layoutXProperty().addListener(propertyListeners[0]);

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                polygonViewController.moveBy(0,newValue-oldValue);

                //update the outline
                polygonViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                //get the reference to the workspace
                Workspace workspace = polygonViewController.getCompositionViewController().getWorkspace();

                //get the reference to the selected items
                SelectedItems selectedItems = workspace.getSelectedItems();

                //create a new item set from the SelectedItems (uses old if it exists)
                Set<ItemViewController> itemSetForNewCommand = selectedItems.getItemSetForNewCommand();

                //create the initial and final points by supplying the same x across both points
                Point2D initialPoint=new Point2D(polygonViewController.getTranslation().getX(),initialValue);
                Point2D finalPoint=new Point2D(polygonViewController.getTranslation().getX(),finalValue);

                //push the move command for shifting vertically,without executing it
                MoveItemSet shiftVertically=new MoveItemSet(itemSetForNewCommand,selectedItems,initialPoint,finalPoint);
                workspace.pushCommand(shiftVertically, !dragged);
            }
        });
        yValue.setStep(1);
        yValue.setValue(polygonViewController.getPolygonView().getLayoutY());

        //array has been instantiated above (if needed)
        propertyListeners[1] = ((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        polygonViewController.getPolygonView().layoutYProperty().addListener(propertyListeners[1]);
        return new HBox(xValue,new Label(","),yValue);
    }

    private HBox getAnchorPointValueNode(){//TODO anchor points not supported yet
        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

            }
        });
        xValue.setStep(1);
        xValue.setValue(polygonViewController.getPolygonView().getTranslateX());
        polygonViewController.getPolygonView().translateXProperty().addListener((observable, oldValue, newValue) -> {
            xValue.setValue(newValue.doubleValue());
        });

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

            }
        });
        yValue.setStep(1);
        yValue.setValue(polygonViewController.getPolygonView().getTranslateY());
        polygonViewController.getPolygonView().translateYProperty().addListener((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        return new HBox(xValue,new Label(","),yValue);
    }

    private HBox getVertexCountValueNode(){
        TextField textField=new TextField(""+polygonViewController.getPolygonView().getPolygonPoints().size());
        textField.setDisable(true);
        return new HBox(textField);
    }

}
