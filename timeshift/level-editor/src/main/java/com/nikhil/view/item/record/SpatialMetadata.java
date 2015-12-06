package com.nikhil.view.item.record;

import com.nikhil.command.composite.ItemCompositeCommand;
import com.nikhil.command.item.*;
import com.nikhil.command.keyframe.AddSpatialKeyframe;
import com.nikhil.command.keyframe.DisableStopWatch;
import com.nikhil.command.keyframe.EnableStopWatch;
import com.nikhil.command.keyframe.ModifySpatialKeyframe;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.editor.workspace.Workspace;
import com.nikhil.space.bezier.path.BezierPoint;
import com.nikhil.timeline.change.spatial.SpatialKeyframeChangeNode;
import com.nikhil.timeline.keyframe.SpatialKeyframe;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.keyframe.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Created by NikhilVerma on 11/11/15.
 */
public class SpatialMetadata extends Metadata {

    private ItemViewController itemViewController;
    private SpatialKeyframePane spatialKeyframePane;
    private SpatialKeyframeChangeNode spatialKeyframeChangeNode;

    private CheckBox keyframable;
    private AddSpatialKeyframe recentAddKeyframeCommand;
    private ModifySpatialKeyframe recentModifyKeyframeCommand;
    
    //TODO we might not need these, on remove from the scene graph , the buttons will be garbage collected
    private EventHandler<ActionEvent> selectPreviousKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        final KeyframeView previousKeyframe = getKeyframePane().selectPreviousKeyframe();
        if(previousKeyframe!=null){
            itemViewController.getCompositionViewController().getPlayback().seekTo(previousKeyframe.getTime());
        }
    };
    private EventHandler<ActionEvent> selectNextKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        final KeyframeView nextKeyframe = getKeyframePane().selectNextKeyframe();
        if(nextKeyframe!=null){
            itemViewController.getCompositionViewController().getPlayback().seekTo(nextKeyframe.getTime());
        }
    };
    private EventHandler<ActionEvent> addManualKeyframe=e->{

        if(!itemViewController.isInteractive()){
            return;
        }

        if (isKeyframable()) {

            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();
            //find a keyframe near that time
            SpatialKeyframe nearbyKeyframe = spatialKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            //create a command to add a manual keyframe at this time
            if(nearbyKeyframe==null){
                SpatialKeyframeView newKeyframe = createNewKeyframe(spatialKeyframeChangeNode.getCurrentPoint(), currentTime);
                recentAddKeyframeCommand = new AddSpatialKeyframe(newKeyframe,true);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(recentAddKeyframeCommand);
            }
            //else ignore if a nearby keyframe already exists

        }
    };

    private ChangeListener<? super Number>[]propertyListeners=null;//hold reference to prevent it from being garbage collected


    public SpatialMetadata(MetadataTag tag,SpatialKeyframeChangeNode spatialKeyframeChangeNode,ItemViewController itemViewController) {
        super(tag.toString(), tag);

        //TODO throw exception if inappropriate tag is given for a controller
        //for example rotation tag for a spatial metadata
        this.itemViewController=itemViewController;
        this.spatialKeyframeChangeNode=spatialKeyframeChangeNode;
        this.keyframable=new CheckBox();
        this.keyframable.setSelected(!spatialKeyframeChangeNode.isEmpty());
        this.keyframable.setOnAction(event -> {
            if(!itemViewController.isInteractive()){
                //undo the selection that just got made
                keyframable.setSelected(!keyframable.isSelected());
                return;
            }
            if(keyframable.isSelected()){
                double currentTime=itemViewController.getCompositionViewController().getTime();
                SpatialKeyframeView newKeyframe = createNewKeyframe(spatialKeyframeChangeNode.getCurrentPoint(), currentTime);
                EnableStopWatch enableStopWatch = new EnableStopWatch(newKeyframe);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(enableStopWatch);
            }else{
                DisableStopWatch disableStopWatch=new DisableStopWatch(this);
                itemViewController.getCompositionViewController().getWorkspace().pushCommand(disableStopWatch);
            }
        });
        initKeyframePane(itemViewController.getCompositionViewController().getTimelineWidth());
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
    public void setKeyframable(boolean keyframable) {
        this.keyframable.setSelected(keyframable);
    }

    @Override
    public Node getValueNode() {
        switch (tag){
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
        if(spatialKeyframePane!=null){
            return spatialKeyframePane;
        }
        CompositionViewController composition = itemViewController.getCompositionViewController();
        spatialKeyframePane = new SpatialKeyframePane(composition.getDuration()
                , width,this);
        spatialKeyframePane.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            ((DoubleProperty)observable).set(0);//downcast to double because we know that layoutx is a double property
        });
        return spatialKeyframePane;
    }

    @Override
    public SpatialKeyframePane getKeyframePane() {
        return spatialKeyframePane;
    }

    public SpatialKeyframeChangeNode getSpatialKeyframeChangeNode() {
        return spatialKeyframeChangeNode;
    }

    private HBox getTranslationValueNode(){

        DraggableTextValue xValue=new DraggableTextValue(new DraggableTextValueDelegate() {

            @Override
            public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {

                UtilPoint oldTranslation = new UtilPoint(itemViewController.getTranslation());
                oldTranslation.setX(oldValue);
                UtilPoint newTranslation = new UtilPoint(itemViewController.getTranslation());
                newTranslation.setX(newValue);

                itemViewController.moveTo(newTranslation.getX(),oldTranslation.getY());
                registerContinuousChange(oldTranslation,newTranslation,null);
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

                //create the initial and final points by supplying the same y across both points
                UtilPoint initialPoint=new UtilPoint(initialValue,itemViewController.getTranslation().getY());
                UtilPoint finalPoint=new UtilPoint(finalValue,itemViewController.getTranslation().getY());

                //push the move command for shifting horizontally,without executing it
                MoveItem shiftHorizontally=new MoveItem(itemViewController, initialPoint,finalPoint);
                endContinuousChange(shiftHorizontally,!dragged,null);
            }

            @Override
            public boolean isEnabled() {
                return itemViewController.isInteractive();
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
                UtilPoint oldTranslation = new UtilPoint(itemViewController.getTranslation());
                oldTranslation.setY(oldValue);
                UtilPoint newTranslation = new UtilPoint(itemViewController.getTranslation());
                newTranslation.setY(newValue);

                itemViewController.moveTo(oldTranslation.getX(),newTranslation.getY());
                registerContinuousChange(oldTranslation,newTranslation,null);
                
                //update the outline
                itemViewController.getCompositionViewController().getWorkspace().getSelectedItems().updateView();
            }

            @Override
            public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {

                //create the initial and final points by supplying the same x across both points
                UtilPoint initialPoint=new UtilPoint(itemViewController.getTranslation().getX(),initialValue);
                UtilPoint finalPoint=new UtilPoint(itemViewController.getTranslation().getX(),finalValue);

                //push the move command for shifting vertically,without executing it
                MoveItem shiftVertically=new MoveItem(itemViewController, initialPoint,finalPoint);
                endContinuousChange(shiftVertically,!dragged,null);
            }

            @Override
            public boolean isEnabled() {
                return itemViewController.isInteractive();
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

            @Override
            public boolean isEnabled() {
                return itemViewController.isInteractive();
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

            @Override
            public boolean isEnabled() {
                return itemViewController.isInteractive();
            }
        });
        yValue.setStep(1);
        yValue.setValue(itemViewController.getItemView().getTranslateY());
        itemViewController.getItemView().translateYProperty().addListener((observable, oldValue, newValue) -> {
            yValue.setValue(newValue.doubleValue());
        });
        return new HBox(xValue,new Label(","),yValue);
    }

    /**
     * Creates and returns a new keyframe view
     * @param anchorPoint the anchorPoint of the bezier point of the keyframe
     * @param time the time of the keyframe
     * @return a temporal keyframe view
     */
    protected SpatialKeyframeView createNewKeyframe(UtilPoint anchorPoint, double time) {
        SpatialKeyframe keyframeModel = new SpatialKeyframe(time, anchorPoint);
        return new SpatialKeyframeView(keyframeModel,spatialKeyframePane);
    }

    /**
     * Reports if the command being supplied just got executed as a result of a previous command operation.
     * This works by checking it it is added to a composite command, (if its not null)
     * @param command the command to check against.
     * @param compositeCommand the composite command that may contain the command just executed.If this is null,
     *                         the command will be checked against the top of the workspace's command stack.
     * @return true if the command was just executed, false otherwise.
     */
    private boolean wasPreviouslyExecuted(ItemCommand command, ItemCompositeCommand compositeCommand){
        if(compositeCommand!=null){
            return compositeCommand.contains(command);
        }else{
            Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();
            return workspace.peekCommandStack()==command;
        }
    }

    /**
     * Pushes the supplied command as part of a composite command(if needed) or to the workspace itself.
     * @param command the command that needs to be pushed
     * @param actionPending weather this command will be executed on being pushed or not
     * @param compositeCommand the composite command to which this command will be added. If this is null,
     *                         the command will be added to the workspace itself.
     */
    private void push(ItemCommand command,boolean actionPending,ItemCompositeCommand compositeCommand){
        if(compositeCommand!=null){
            compositeCommand.addItemCommand(command,actionPending);
        }else{
            Workspace workspace = itemViewController.getCompositionViewController().getWorkspace();
            workspace.pushCommand(command,actionPending);
        }
    }
    /**
     * For any change on the observed property, calling this method will
     * add or modify a keyframe command (if the property is keyframabale).
     * This method <b>must</b> be called for every continuous change. Ex: like the
     * ones that happen inside a mouse drag event
     * @param oldPoint the last position of the continuous change
     * @param newPoint the new position of the continuous change
     * @param compositeCommand the composite command that will contain the resulting command.This can also be null.
     *                             If its null, the resulting command will be pushed to the workspace instead.
     */
    public void registerContinuousChange(UtilPoint oldPoint, UtilPoint newPoint, ItemCompositeCommand compositeCommand){
        if(isKeyframable()){

            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();

            //find a keyframe near that time
            SpatialKeyframe nearbyKeyframe = spatialKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            //if a keyframe does exists, modify it
            if (nearbyKeyframe!=null) {

                //save the last bezier point in a new instance, we will use this in our modify command
                BezierPoint lastBezierPoint= new BezierPoint(nearbyKeyframe.getBezierPoint());
                nearbyKeyframe.getBezierPoint().setAnchorPoint(newPoint);//besides this, we also need to maintain keyframe commands

                //if the last action was to add a keyframe
                if(wasPreviouslyExecuted(recentAddKeyframeCommand,compositeCommand)){

                    //create a modify keyframe command if the add keyframe command is complete
                    if (recentAddKeyframeCommand.isComplete()) {
                        SpatialKeyframeView keyframeView = spatialKeyframePane.findKeyframeView(nearbyKeyframe);
                        recentModifyKeyframeCommand=new ModifySpatialKeyframe(keyframeView,lastBezierPoint);

                        //push command ,but don't execute, because this change has already been done
                        push(recentModifyKeyframeCommand,false,compositeCommand);
                    }else{
                        //update the motion path of the affected keyframe
                        if(nearbyKeyframe==recentAddKeyframeCommand.getKeyframeCreated().getKeyframeModel()){
                            recentAddKeyframeCommand.getKeyframeCreated().updateMotionPath();
                        }else{
                            spatialKeyframePane.findKeyframeView(nearbyKeyframe).updateMotionPath();
                        }
                    }
                }
                //last action was to modify a keyframe's view
                else if(wasPreviouslyExecuted(recentModifyKeyframeCommand,compositeCommand)){

                    //create a new modify keyframe command if the current modify command is complete
                    if (recentModifyKeyframeCommand.isComplete()) {
                        SpatialKeyframeView keyframeView = spatialKeyframePane.findKeyframeView(nearbyKeyframe);
                        recentModifyKeyframeCommand=new ModifySpatialKeyframe(keyframeView,lastBezierPoint);

                        //push command ,but don't execute, because this change has already been done
                        push(recentModifyKeyframeCommand,false,compositeCommand);
                    }else{
                        //update the motion path of the affected keyframe
                        if(nearbyKeyframe==recentModifyKeyframeCommand.getSpatialKeyframeView().getKeyframeModel()){
                            recentModifyKeyframeCommand.getSpatialKeyframeView().updateMotionPath();
                        }else{
                            spatialKeyframePane.findKeyframeView(nearbyKeyframe).updateMotionPath();
                        }
                    }

                }else{
                    //create a modify keyframe command and push it
                    SpatialKeyframeView keyframeView = spatialKeyframePane.findKeyframeView(nearbyKeyframe);
                    recentModifyKeyframeCommand=new ModifySpatialKeyframe(keyframeView,lastBezierPoint);

                    //push command ,but don't execute, because this change has already been done
                    push(recentModifyKeyframeCommand,false,compositeCommand);
                }
            }
            //if no nearby keyframes exists , we need to create a new one
            else{
                //reset any selection made and add a keyframe at this change
                SpatialKeyframeView newKeyframe = createNewKeyframe(oldPoint, currentTime);
                recentAddKeyframeCommand = new AddSpatialKeyframe(newKeyframe,false);
                push(recentAddKeyframeCommand,true,compositeCommand);
            }
        }
    }

    /**
     * Pushes the supplied command as a "continuous command" of an existing keyframe command.
     * The Keyframe command itself is added to the composite command
     * This method works on the single item that this metadata represents.If the command
     * is an {@link ActionOnItemSet} (ex: {@link MoveItemSet}, then this method should not be called for items
     * in the "item set".
     * @param action a spatial action that just occurred on a single item(for example: {@link MoveItem})
     * @param actionPending The action itself will be executed only if this flag is true
     * @param compositeCommand the composite command that will contain the resulting command.This can also be null.
     *                             If its null, the resulting command will be pushed to the workspace instead.
     * @return true if the keyframe command is pending and needs execution.False otherwise
     */
    public void endContinuousChange(SpatialActionOnSingleItem action,boolean actionPending,ItemCompositeCommand compositeCommand){
        if(isKeyframable()){

            //this can happen if, lets say, the user directly enters a value in the textfield
            if(actionPending){
                pushDirectChangeWithKeyframe(action,compositeCommand);
            }
            //if the action is not pending it implies the "end" of a continuous change
            else{
                //check if the top most command is an add keyframe operation
                if(wasPreviouslyExecuted(recentAddKeyframeCommand,compositeCommand)){

                    //manual keyframes don't introduce any change ,they just add new keyframe
                    if (recentAddKeyframeCommand.isManual()) {
                        //create a new "recently modified keyframe" command,
                        //use the keyframe view from the recently added keyframe command
                        recentModifyKeyframeCommand=new ModifySpatialKeyframe(
                                recentAddKeyframeCommand.getKeyframeCreated(),
                                action.getInitialPoint(),
                                action.getFinalPoint(),
                                action);
                        push(recentModifyKeyframeCommand,false,compositeCommand);
//                        workspace.pushCommand(recentModifyKeyframeCommand,false);
                    } else {
                        //update the "end" of the continuous change
                        recentAddKeyframeCommand.setContinuousCommand(action);
                        recentAddKeyframeCommand.setFinal(action.getFinalPoint());
                    }
                }else if(wasPreviouslyExecuted(recentModifyKeyframeCommand,compositeCommand)){
                    //modify the "recently modified keyframe" command,
                    recentModifyKeyframeCommand.setContinuousCommand(action);
                    //keep in mind that the initial value has already been set,
                    //here we set the final value for the modify keyframe command
                    recentModifyKeyframeCommand.setFinal(action.getFinalPoint());
                }else{
                    //most unlikely case, this implies that a continuous change was not
                    //registered with this metadata or that something is wrong with the register method
                    throw new RuntimeException("Previously executed command is not compatible with the " +
                            "end of this continuous change. Make sure the changes are " +
                            "'REGISTERED' to the spatial metadata and that the composite command is the same in both calls");
                }
            }
        }else{
            push(action,actionPending,compositeCommand);
        }
    }


    /**
     * Takes care of a direct change where the initial and final point of the change are known
     * in one go.It is understood the the action is pending.
     * @param action the action for that change which has not been pushed to the command stack yet.
     * @param compositeCommand if its not null, action will be a part of the composite command, else it
     *                         will be added to the workspace.
     *
     */
    public void pushDirectChangeWithKeyframe(SpatialActionOnSingleItem action,ItemCompositeCommand compositeCommand){

        if (!isKeyframable()) {
            //just push the command, and execute it
            push(action,true,compositeCommand);
        }else{
            //get the current time of the composition
            double currentTime=itemViewController.getCompositionViewController().getTime();

            //find a keyframe near that time
            SpatialKeyframe nearbyKeyframe = spatialKeyframeChangeNode.findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            if(nearbyKeyframe!=null){
                //issue modify existing keyframe command
                SpatialKeyframeView keyframeView = spatialKeyframePane.findKeyframeView(nearbyKeyframe);
                recentModifyKeyframeCommand=new ModifySpatialKeyframe(keyframeView,action.getInitialPoint(),
                        action.getFinalPoint(),action);

                //push and execute
                push(recentModifyKeyframeCommand,true,compositeCommand);

            }else{
                //issue a add new keyframe command
                SpatialKeyframeView newKeyframe = createNewKeyframe(action.getFinalPoint(), currentTime);
                recentAddKeyframeCommand =new AddSpatialKeyframe(newKeyframe, action);
                //push and execute
                push(recentAddKeyframeCommand,true,compositeCommand);
            }
        }
    }
}
