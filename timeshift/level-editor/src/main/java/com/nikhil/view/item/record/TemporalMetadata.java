package com.nikhil.view.item.record;

import com.nikhil.command.RotateShape;
import com.nikhil.command.ScaleShape;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.math.MathUtil;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.keyframe.KeyframePane;
import com.nikhil.view.custom.keyframe.TemporalKeyframePane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalMetadata extends Metadata{

    private ItemViewController itemViewController;
    private TemporalKeyframePane keyframePane;

    private ChangeListener<? super Number>[]propertyListeners=null;//hold reference to prevent it from being garbage collected
    //TODO we might not need these, on remove from the scene graph , the buttons will be garbage collected
    private EventHandler<ActionEvent> selectPreviousKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectPreviousKeyframe();
    };
    private EventHandler<ActionEvent> selectNextKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectNextKeyframe();
    };

    public TemporalMetadata(String name, MetadataTag tag,ItemViewController itemViewController) {
        super(name, tag);
        //TODO throw exception if inappropriate tag is given for a controller
        //for example rotation tag for a travel path
        this.itemViewController=itemViewController;
    }

    @Override
    public ItemViewController getItemViewController() {
        return itemViewController;
    }

    @Override
    public Node getValueNode() {
        switch (tag){
            case SCALE:
                return getScaleValueNode();
            case ROTATION:
                return getRotationValueNode();
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
    public KeyframePane initKeyframePane(double width){
//        if(keyframePane!=null){
//            return keyframePane;
//        }
//        keyframePane = new TemporalKeyframePane(30, width,this);//TODO remove hardcode
//        int r=random.nextInt(10);
//        for (int i = 0; i < r; i++) {
//            keyframePane.addKeyAt(random.nextInt(30), null);
//        }
//        keyframePane.layoutXProperty().addListener((observable, oldValue, newValue) -> {
//            ((DoubleProperty)observable).set(0);//downcast to double because we know that layoutx is a double property
//        });
        return keyframePane;
    }

    @Override
    public KeyframePane getKeyframePane() {
        return keyframePane;
    }

    private HBox getScaleValueNode(){
        if(!(itemViewController instanceof ShapeViewController)){
            throw new RuntimeException("Scale changes are only defined on shapes");
        }
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                itemViewController.scaleBy(newValue - oldValue);
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                ScaleShape scaleShape =new ScaleShape((ShapeViewController)itemViewController,initialValue,finalValue);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(scaleShape,!dragged);
            }
        });
        draggableTextValue.setLowerLimit(0);
        draggableTextValue.setLowerLimitExists(true);
        draggableTextValue.setStep(0.01);
        draggableTextValue.setValue(itemViewController.getItemView().getScaleX());
        if(propertyListeners !=null){
            itemViewController.getItemView().scaleXProperty().removeListener(propertyListeners[0]);
        }
        propertyListeners = new ChangeListener[1];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });
        itemViewController.getItemView().scaleXProperty().addListener(propertyListeners[0]);
        return new HBox(draggableTextValue);
    }

    private HBox getRotationValueNode(){
        if(!(itemViewController instanceof ShapeViewController)){
            throw new RuntimeException("Rotation changes are only defined on shapes");
        }
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                double step=newValue-oldValue;
                itemViewController.rotateBy(step);
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
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
                RotateShape rotateShape=new RotateShape(((ShapeViewController)itemViewController),initialValue,finalValue);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(rotateShape,!dragged);
            }
        });
        draggableTextValue.setStep(1);
        draggableTextValue.setValue(itemViewController.getItemView().getRotate());

        if(propertyListeners !=null){
            itemViewController.getItemView().rotateProperty().removeListener(propertyListeners[0]);
        }
        propertyListeners = new ChangeListener[1];
        propertyListeners[0] = ((observable, oldValue, newValue) -> {
            draggableTextValue.setValue(newValue.doubleValue());
        });

        itemViewController.getItemView().rotateProperty().addListener(propertyListeners[0]);
        return new HBox(draggableTextValue);
    }

}
