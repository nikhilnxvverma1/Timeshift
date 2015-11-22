package com.nikhil.command;

import com.nikhil.timeline.KeyValue;
import com.nikhil.timeline.keyframe.TemporalKeyframe;
import com.nikhil.view.custom.keyframe.TemporalKeyframeView;
import com.nikhil.view.item.record.TemporalMetadata;

/**
 * Command that adds a keyframe. This command can be of two types: manual or continuous.
 * <p>
 * Manual keyframes don't introduce any change, they just add a new keyframe.
 * The initial value and final value for this change is the same.
 * </p>
 * <p>
 * Continuous keyframe implies a change taking place along with the addition of this keyframe.
 * The initial and final value are initially the same. But at the end of a continuos change,
 * the final value is <b>supposed</b> to be set. Make sure to call {@code setFinalValue()} at the end of
 * the change.Continuous commands also require a "continuous command" that will be executed inside
 * this command.This <b>must</b> also be set at the end of a change using {@code setContinuousCommand()}
 * </p>
 *
 * <p>
 *     Continuous commands can also happen all at once, where the initial and final value along
 *     with the action("Continuous Command") are known in one go.
 * </p>
 *
 * Created by NikhilVerma on 13/11/15.
 */
public class AddTemporalKeyframe implements Command{

    private boolean manual;
    private TemporalKeyframeView keyframeCreated;
    private TemporalActionOnSingleItem continuousCommand;

    /**
     * Creates a add temporal keyframe command
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param manual false, if there is some continuous command that is allowed to follow this command.
     *                       For example: rotation command will happen after a rotation keyframe has been added.
     *                       If this keyframe is created through a manually(lets say through a add keyframe button),
     *                       this value should be true.
     */
    public AddTemporalKeyframe(TemporalKeyframeView keyframeCreated, boolean manual) {
        this.keyframeCreated=keyframeCreated;
        this.manual = manual;//TODO this will always be false, if this constructor is invoked
    }

    /**
     * Creates a continuous add temporal keyframe command with final values known
     * @param keyframeCreated the keyframe view just created that needs to be added
     * @param continuousCommand the command that will be executed along with this add keyframe command
     */
    public AddTemporalKeyframe(TemporalKeyframeView keyframeCreated, TemporalActionOnSingleItem continuousCommand) {
        this.keyframeCreated=keyframeCreated;
        this.continuousCommand=continuousCommand;
        this.manual = true;
    }

    @Override
    public void execute() {

        //reset the selection of keyframes
        keyframeCreated.getKeyframePane().getMetadata().getItemViewController().
                getCompositionViewController().getKeyframeTable().resetSelection();
        keyframeCreated.addToParentKeyframePane();
        if(continuousCommand!=null){
            continuousCommand.execute();
        }
    }

    @Override
    public void unexecute() {
        if(continuousCommand!=null){
            continuousCommand.unexecute();//this will take care of reverting back to initial value
        }
        keyframeCreated.removeFromParentKeyframePane();
    }

    public TemporalActionOnSingleItem getContinuousCommand() {
        return continuousCommand;
    }

    /**
     * Executes and unexecutes a continuous command along with "this" command.
     * This is only allowed if this is not a manual keyframe
     * @param continuousCommand  Do not set a new continuous command if one already
     *                           exists and "this" command is on stack.
     */
    public void setContinuousCommand(TemporalActionOnSingleItem continuousCommand) {
        if(manual){
            throw new RuntimeException("Continuous commands can't be added to manual keyframes");
        }
        this.continuousCommand = continuousCommand;
    }

    /**
     * Returns false, if this add command is continuous and is going to be followed by an end.
     * For example: rotation command will happen after a rotation keyframe has been added.
     * If this keyframe is created manually(lets say through a add keyframe button),
     * this method will return true.
     */
    public boolean isManual() {
        return manual;
    }

    public TemporalKeyframeView getKeyframeCreated() {
        return keyframeCreated;
    }

    /**
     * Sets the final value of the keyframe.For non manual commands,
     * this method MUST be called at the end of a "continuous" change.
     * @param finalValue the final value of the keyframe when a continuous change ends
     */
    public void setFinalValue(KeyValue finalValue) {
        keyframeCreated.getKeyframeModel().setKeyValue(finalValue);
    }

    /**
     * Checks if this command has been filled or not
     * @return true if this 'Add Keyframe' command received the end of the change
     */
    public boolean isComplete(){
        return manual||continuousCommand!=null;
    }
}
