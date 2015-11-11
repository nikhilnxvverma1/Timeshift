package com.nikhil.view.item.record;

import com.nikhil.command.MoveItemSet;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.keyframe.KeyframePane;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Set;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialMetadata extends Metadata {

    private ItemViewController itemViewController;

    //TODO we might not need these, on remove from the scene graph , the buttons will be garbage collected
    private EventHandler<ActionEvent> selectPreviousKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectPreviousKeyframe();
    };
    private EventHandler<ActionEvent> selectNextKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectNextKeyframe();
    };

    private ChangeListener<? super Number>[]propertyListeners=null;//hold reference to prevent it from being garbage collected
    public SpatialMetadata(String name, MetadataTag tag,ItemViewController itemViewController) {
        super(name, tag);
        //TODO throw exception if inappropriate tag is given for a controller
        //for example rotation tag for a spatial metadata
        this.itemViewController=itemViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return itemViewController;
    }

    @Override
    public Node getValueNode() {
        switch (tag){
            case HEADER:
                return new Button("Reset");//TODO delegation and visual size
            case TRANSLATION:
                return getTranslationValueNode();
            case ANCHOR_POINT:
                return getAnchorPointValueNode();
        }
        return null;
    }

    @Override
    public Node getOptionNode() {

        Button previousKeyframe = new Button("<");
        previousKeyframe.setOnAction(selectPreviousKeyframe);
        Tooltip.install(previousKeyframe, new Tooltip("Previous Keyframe"));

        ToggleButton toggleButton = new ToggleButton("*");
        Tooltip.install(toggleButton, new Tooltip("Toggle Keyframe"));

        Button nextKeyframe = new Button(">");
        nextKeyframe.setOnAction(selectNextKeyframe);
        Tooltip.install(nextKeyframe, new Tooltip("Next Keyframe"));
        return new HBox(previousKeyframe, toggleButton, nextKeyframe);
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public KeyframePane initKeyframePane(double width) {
        return null;
    }

    @Override
    public KeyframePane getKeyframePane() {
        return null;
    }

    private HBox getTranslationValueNode(){
        if(!(itemViewController instanceof ShapeViewController)){
            throw new RuntimeException("Translation for a complete entity is only applicable for shapes");
        }
        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                itemViewController.moveBy(newValue-oldValue,0);
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

                //get the reference to the workspace
                Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();

                //get the reference to the selected items
                SelectedItems selectedItems = workspace.getSelectedItems();

                //create a new item set from the SelectedItems (uses old if it exists)
                Set<ItemViewController> itemSetForNewCommand = selectedItems.getItemSetForNewCommand();

                //create the initial and final points by supplying the same y across both points
                Point2D initialPoint=new Point2D(initialValue,((ShapeViewController)itemViewController).getTranslation().getY());
                Point2D finalPoint=new Point2D(finalValue,((ShapeViewController)itemViewController).getTranslation().getY());

                //push the move command for shifting horizontally,without executing it
                MoveItemSet shiftHorizontally=new MoveItemSet(itemSetForNewCommand,selectedItems,initialPoint,finalPoint);
                workspace.pushCommand(shiftHorizontally, !dragged);
            }
        });
        xValue.setStep(1);
        xValue.setValue(itemViewController.getItemView().getLayoutX());

        if((propertyListeners !=null)&&(propertyListeners.length==2)){
            itemViewController.getItemView().layoutXProperty().removeListener(propertyListeners[0]);
            itemViewController.getItemView().layoutYProperty().removeListener(propertyListeners[1]);
        }
        propertyListeners = new ChangeListener[2];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            xValue.setValue(newValue.doubleValue());
        });

        itemViewController.getItemView().layoutXProperty().addListener(propertyListeners[0]);

        DraggableTextValue yValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                itemViewController.moveBy(0,newValue-oldValue);

                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                //get the reference to the workspace
                Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();

                //get the reference to the selected items
                SelectedItems selectedItems = workspace.getSelectedItems();

                //create a new item set from the SelectedItems (uses old if it exists)
                Set<ItemViewController> itemSetForNewCommand = selectedItems.getItemSetForNewCommand();

                //create the initial and final points by supplying the same x across both points
                Point2D initialPoint=new Point2D(((ShapeViewController)itemViewController).getTranslation().getX(),initialValue);
                Point2D finalPoint=new Point2D(((ShapeViewController)itemViewController).getTranslation().getX(),finalValue);

                //push the move command for shifting vertically,without executing it
                MoveItemSet shiftVertically=new MoveItemSet(itemSetForNewCommand,selectedItems,initialPoint,finalPoint);
                workspace.pushCommand(shiftVertically, !dragged);
            }
        });
        yValue.setStep(1);
        yValue.setValue(itemViewController.getItemView().getLayoutY());

        //array has been instantiated above (if needed)
        propertyListeners[1] = ((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        itemViewController.getItemView().layoutYProperty().addListener(propertyListeners[1]);
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
        xValue.setValue(itemViewController.getItemView().getTranslateX());
        itemViewController.getItemView().translateXProperty().addListener((observable, oldValue, newValue) -> {
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
        yValue.setValue(itemViewController.getItemView().getTranslateY());
        itemViewController.getItemView().translateYProperty().addListener((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        return new HBox(xValue,new Label(","),yValue);
    }

}
