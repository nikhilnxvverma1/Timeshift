package com.nikhil.view.item.record;

import com.nikhil.command.item.RotateShape;
import com.nikhil.command.item.ScaleShape;
import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.command.keyframe.AddTemporalKeyframe;
import com.nikhil.command.keyframe.DisableStopWatch;
import com.nikhil.command.keyframe.EnableStopWatch;
import com.nikhil.command.keyframe.ModifyTemporalKeyframe;
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
import com.nikhil.view.custom.keyframe.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Metadata for any temporal property. <b>After instantiating, make sure to
 * call setValueNode to supply the required node that controls this property.</b>
 * Created by NikhilVerma on 11/11/15.
 */
public class TemporalMetadata extends PropertyMetadata{

    private TemporalKeyframePane temporalKeyframePane;
    private TemporalKeyframeChangeNode temporalKeyframeChangeNode;
    private Node valueNode;

    private AddTemporalKeyframe recentAddKeyframeCommand;
    private ModifyTemporalKeyframe recentModifyKeyframeCommand;

    public TemporalMetadata(MetadataTag tag,TemporalKeyframeChangeNode temporalKeyframeChangeNode,ItemViewController itemViewController) {
        super(itemViewController, tag);
        this.temporalKeyframeChangeNode=temporalKeyframeChangeNode;
        postInit();
    }

    @Override
    public TemporalKeyframeChangeNode getKeyframeChangeNode() {
        return temporalKeyframeChangeNode;
    }

    @Override
    public void pushManualKeyframeCommand(double time) {
        TemporalKeyframeView newKeyframe = createNewKeyframe(temporalKeyframeChangeNode.getCurrentValue(), time);
        recentAddKeyframeCommand = new AddTemporalKeyframe(newKeyframe,true);
        itemViewController.getCompositionViewController().getWorkspace().pushCommand(recentAddKeyframeCommand);
    }

    @Override
    protected KeyframeView createNewKeyframe(double time) {
        return createNewKeyframe(temporalKeyframeChangeNode.getCurrentValue(),time);
    }

    @Override
    public Node getValueNode() {
        return valueNode;
    }

    public void setValueNode(Node valueNode) {
        this.valueNode = valueNode;
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

    /**
     * Creates and returns a new keyframe view
     * @param keyValue the value of the keyframe
     * @param time the time of the keyframe
     * @return a temporal keyframe view
     */
    protected TemporalKeyframeView createNewKeyframe(KeyValue keyValue, double time) {
        TemporalKeyframe keyframeModel = new TemporalKeyframe(time, keyValue);
        return new TemporalKeyframeView(keyframeModel,temporalKeyframePane);
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
                TemporalKeyframeView newKeyframe = createNewKeyframe(oldKeyValue, currentTime);
                recentAddKeyframeCommand = new AddTemporalKeyframe(newKeyframe,false);
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
                TemporalKeyframeView newKeyframe = createNewKeyframe(action.getFinalValue(), currentTime);
                recentAddKeyframeCommand =new AddTemporalKeyframe(newKeyframe, action);
                //push and execute
                workspace.pushCommand(recentAddKeyframeCommand);
            }
        }
    }
}
