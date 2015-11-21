package com.nikhil.view.item.record;

import com.nikhil.command.*;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.math.MathUtil;
import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.change.temporal.TemporalKeyframeChangeNode;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.keyframe.KeyframePane;
import com.nikhil.view.custom.keyframe.TemporalKeyframePane;
import com.nikhil.view.custom.keyframe.TemporalKeyframeView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalMetadata extends Metadata{

    private ItemViewController itemViewController;
    private TemporalKeyframePane temporalKeyframePane;
    private TemporalKeyframeChangeNode temporalKeyframeChangeNode;

    private CheckBox keyframable;
    private TemporalKeyframe savedStart,savedLast;
    private ChangeListener<? super Number>[]propertyListeners=null;//hold reference to prevent it from being garbage collected
    private AddTemporalKeyframe recentAddKeyframeCommand;
    private ModifyTemporalKeyframe recentModifyKeyframeCommand;

    //TODO we might not need these, on remove from the scene graph , the buttons will be garbage collected
    private EventHandler<ActionEvent> selectPreviousKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectPreviousKeyframe();
    };
    private EventHandler<ActionEvent> selectNextKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        getKeyframePane().selectNextKeyframe();
    };
    private EventHandler<ActionEvent> addManualKeyframe=e->{
        if (isKeyframable()) {

            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();
            //find a keyframe near that time
            TemporalKeyframe nearbyKeyframe = temporalKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            //create a command to add a manual keyframe at this time
            if(nearbyKeyframe==null){
                recentAddKeyframeCommand = new AddTemporalKeyframe(this, currentTime,
                        temporalKeyframeChangeNode.getCurrentValue(),true);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(recentAddKeyframeCommand);
            }
            //else ignore if a nearby keyframe already exists

        }
    };

    public TemporalMetadata(MetadataTag tag,TemporalKeyframeChangeNode temporalKeyframeChangeNode,ItemViewController itemViewController) {
        super(tag.toString(), tag);
        //TODO throw exception if inappropriate tag is given for a controller
        //for example rotation tag for a travel path
        this.temporalKeyframeChangeNode=temporalKeyframeChangeNode;
        this.itemViewController=itemViewController;
        this.keyframable=new CheckBox();
        this.keyframable.setSelected(false);
        this.keyframable.setOnAction(event -> {
            //toggle keyframes
            ToggleTemporalMetadataKeyframes toggleCommand=new ToggleTemporalMetadataKeyframes(this,keyframable.isSelected());
            itemViewController.getCompositionViewController().getWorkspace().pushCommand(toggleCommand);
        });
    }

    public TemporalKeyframeChangeNode getTemporalKeyframeChangeNode() {
        return temporalKeyframeChangeNode;
    }

    @Override
    public ItemViewController getItemViewController() {
        return itemViewController;
    }

    @Override
    public Node getNameNode() {
        return keyframable;
    }

    @Override
    public boolean isKeyframable(){
        return keyframable.isSelected();
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
        toggleButton.setOnAction(addManualKeyframe);
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
        if(temporalKeyframePane!=null){
            return temporalKeyframePane;
        }
        temporalKeyframePane = new TemporalKeyframePane(30, width,this);//TODO remove hardcode
        temporalKeyframePane.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            ((DoubleProperty)observable).set(0);//downcast to double because we know that layoutx is a double property
        });
        return temporalKeyframePane;
    }

    @Override
    public TemporalKeyframePane getKeyframePane() {
        return temporalKeyframePane;
    }

    private HBox getScaleValueNode(){
        if(!(itemViewController instanceof ShapeViewController)){
            throw new RuntimeException("Scale changes are only defined on shapes");
        }
        DraggableTextValue draggableTextValue=new DraggableTextValue(new DraggableTextValueDelegate() {
            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
                double dScale = newValue - oldValue;
                double oldScale=((ShapeViewController) itemViewController).getScale();
                double newScale=itemViewController.scaleBy(dScale);
                registerContinuousChange(new KeyValue(oldScale),new KeyValue(newScale));
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
                ScaleShape scaleShape =new ScaleShape((ShapeViewController)itemViewController,initialValue,finalValue);
//                itemViewController.getCompositionViewController().getWorkspace().pushCommand(scaleShape,!dragged);
                pushWithKeyframe(scaleShape,!dragged);
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
                double oldRotation=((ShapeViewController) itemViewController).getRotation();
                double newRotation = itemViewController.rotateBy(step);
                registerContinuousChange(new KeyValue(oldValue),new KeyValue(newValue));
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
                pushWithKeyframe(rotateShape,!dragged);
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

    public void saveKeyframeConfiguration(){
        savedStart=temporalKeyframeChangeNode.getStart();
        savedLast=temporalKeyframeChangeNode.getLast();
    }
    public void removeAllKeyframes(){
        temporalKeyframeChangeNode.setStart(null);
        temporalKeyframeChangeNode.setLast(null);
        temporalKeyframePane.removeAllKeyframes();
    }
    public void restoreKeyframeConfiguration(){
        temporalKeyframeChangeNode.setStart(savedStart);
        temporalKeyframeChangeNode.setLast(savedLast);
        savedStart=null;
        savedLast=null;
        //TODO rebuild keyframe views
    }

    /**
     * For any change on the observed property, calling this method will
     * add or modify a keyframe command (if the property is keyframabale).
     * This method <b>must</b> be called for every continuous change. Ex: like the
     * ones that happen inside a mouse drag event
     * @param oldKeyValue the last value of this property
     * @param newKeyValue the value for the keyframe to set (or modify)
     */
    public void registerContinuousChange(KeyValue oldKeyValue, KeyValue newKeyValue){
        if(isKeyframable()){

            Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();
            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();

            //find a keyframe near that time
            TemporalKeyframe nearbyKeyframe = temporalKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            //if a keyframe does exists, modify it
            if (nearbyKeyframe!=null) {

                nearbyKeyframe.getKeyValue().set(newKeyValue);//besides this, we also need to maintain keyframe commands

                //if the last action was to add a keyframe
                if(workspace.peekCommandStack()==recentAddKeyframeCommand){

                    //create a modify keyframe command if the add keyframe command is complete
                    if (recentAddKeyframeCommand.isComplete()) {
                        TemporalKeyframeView keyframeView = temporalKeyframePane.findKeyframeView(nearbyKeyframe);
                        recentModifyKeyframeCommand=new ModifyTemporalKeyframe(keyframeView,oldKeyValue);

                        //push command ,but don't execute, because this change has already been done
                        workspace.pushCommand(recentModifyKeyframeCommand, false);
                    }
                }
                //last action was to modify a keyframe's view
                else if(workspace.peekCommandStack()==recentModifyKeyframeCommand){

                    //create a new modify keyframe command if the current modify command is complete
                    if (recentModifyKeyframeCommand.isComplete()) {
                        TemporalKeyframeView keyframeView = temporalKeyframePane.findKeyframeView(nearbyKeyframe);
                        recentModifyKeyframeCommand=new ModifyTemporalKeyframe(keyframeView,oldKeyValue);

                        //push command ,but don't execute, because this change has already been done
                        workspace.pushCommand(recentModifyKeyframeCommand, false);
                    }

                }else{
                    //create a modify keyframe command and push it
                    TemporalKeyframeView keyframeView = temporalKeyframePane.findKeyframeView(nearbyKeyframe);
                    recentModifyKeyframeCommand=new ModifyTemporalKeyframe(keyframeView,oldKeyValue);

                    //push command ,but don't execute, because this change has already been done
                    workspace.pushCommand(recentModifyKeyframeCommand, false);
                }
            }
            //if no nearby keyframes exists , we need to create a new one
            else{
                //reset any selection made and add a keyframe at this change
                recentAddKeyframeCommand = new AddTemporalKeyframe(this, currentTime, oldKeyValue,false);
                workspace.pushCommand(recentAddKeyframeCommand);

            }

        }
    }

    /**
     * Pushes the supplied command to the command stack by composing it in an existing
     * keyframe command.
     * @param action a temporal action that just occurred on a single item(for example: {@link RotateShape})
     * @param actionPending The action itself will be executed only if this flag is true
     */
    public void pushWithKeyframe(TemporalActionOnSingleItem action,boolean actionPending){
        Workspace workspace=itemViewController.getCompositionViewController().getWorkspace();
        if (!isKeyframable()) {
            //just push the command, and execute it if necessary
            workspace.pushCommand(action, actionPending);
        } else {

            //this can happen if, lets say, the user directly enters a value in the textfield
            if(actionPending){
                pushDirectChangeWithKeyframe(action);
            }
            //if the action is not pending it implies the "end" of a continuous change
            else{
                //check if the top most command is an add keyframe operation
                if(workspace.peekCommandStack()==recentAddKeyframeCommand){

                    //manual keyframes don't introduce any change ,they just add new keyframe
                    if (recentAddKeyframeCommand.isManual()) {
                        //create a new "recently modified keyframe" command,
                        //use the keyframe view from the recently added keyframe command
                        recentModifyKeyframeCommand=new ModifyTemporalKeyframe(
                                recentAddKeyframeCommand.getKeyframeCreated(),
                                action.getInitialValue(),
                                action.getFinalValue(),
                                action);
                        workspace.pushCommand(recentModifyKeyframeCommand,false);
                    } else {
                        //update the "end" of the continuous change
                        recentAddKeyframeCommand.setContinuousCommand(action);
                        recentAddKeyframeCommand.setFinalValue(action.getFinalValue());
                    }
                }else if(workspace.peekCommandStack()==recentModifyKeyframeCommand){
                    //modify the "recently modified keyframe" command,
                    recentModifyKeyframeCommand.setContinuousCommand(action);
                    //keep in mind that the initial value has already been set,
                    //here we set the final value for the modify keyrame command
                    recentModifyKeyframeCommand.setFinalValue(action.getFinalValue());
                }else{
                    //most unlikely case, this implies that a continuous change was not
                    //registered with this metadata or that something is wrong with the register method
                    throw new RuntimeException("Top command is not compatible with the " +
                            "end of this continuous change. Make sure the changes are " +
                            "'REGISTERED' to the temporal metadata");
                }
            }
        }
    }

    /**
     * Takes care of a direct change where the initial and final value of the change are known
     * in one go.It is understood the the action is pending.
     * @param action the action for that change which has not been pushed to the command stack yet.
     */
    public void pushDirectChangeWithKeyframe(TemporalActionOnSingleItem action){

        Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();

        if (!isKeyframable()) {
            //just push the command, and execute it
            workspace.pushCommand(action);
        }else{
            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();

            //find a keyframe near that time
            TemporalKeyframe nearbyKeyframe = temporalKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            if(nearbyKeyframe!=null){
                //issue modify existing keyframe command
                TemporalKeyframeView keyframeView = temporalKeyframePane.findKeyframeView(nearbyKeyframe);
                recentModifyKeyframeCommand=new ModifyTemporalKeyframe(keyframeView,action.getInitialValue(),
                        action.getFinalValue(),action);

                //push and execute
                workspace.pushCommand(recentModifyKeyframeCommand);

            }else{
                //issue a add new keyframe command
                recentAddKeyframeCommand =new AddTemporalKeyframe(this, currentTime, action.getInitialValue(),
                        action.getFinalValue(),action);
                //push and execute
                workspace.pushCommand(recentAddKeyframeCommand);
            }
        }
    }
}
