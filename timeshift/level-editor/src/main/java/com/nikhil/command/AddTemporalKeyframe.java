package com.nikhil.command;

import com.nikhil.timeline.KeyValue;
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
    private TemporalMetadata temporalMetadata;
    private double time;
    private KeyValue initialValue;
    private KeyValue finalValue;
    private boolean manual;

    private TemporalKeyframeView keyframeCreated;
    private TemporalActionOnSingleItem continuousCommand;

    /**
     * Creates a add temporal keyframe command
     * @param temporalMetadata metadata invoking this constructor
     * @param time current time of the composition
     * @param initialValue initial value of the temporal keyframe change node
     * @param manual false, if there is some continuous command that is allowed to follow this command.
     *                       For example: rotation command will happen after a rotation keyframe has been added.
     *                       If this keyframe is created through a manually(lets say through a add keyframe button),
     *                       this value should be true.
     */
    public AddTemporalKeyframe(TemporalMetadata temporalMetadata, double time, KeyValue initialValue, boolean manual) {
        this.temporalMetadata = temporalMetadata;
        this.time = time;
        this.initialValue = initialValue;
        this.manual = manual;

        //for non manual keyframes,the final value should will get set at the end of a continuous change
        this.finalValue=initialValue;
    }

    /**
     * Creates a continuous add temporal keyframe command with final values known
     * @param temporalMetadata metadata invoking this constructor
     * @param time current time of the composition
     * @param initialValue initial value of the temporal keyframe change node
     */
    public AddTemporalKeyframe(TemporalMetadata temporalMetadata, double time,
                               KeyValue initialValue,KeyValue finalValue,TemporalActionOnSingleItem continuousCommand) {
        this.temporalMetadata = temporalMetadata;
        this.time = time;
        this.initialValue = initialValue;
        this.finalValue=finalValue;
        this.continuousCommand=continuousCommand;
        this.manual = true;
    }

    @Override
    public void execute() {
        temporalMetadata.getItemViewController().getCompositionViewController().getKeyframeTable().resetSelection();
        keyframeCreated=temporalMetadata.getKeyframePane().addKeyframe(time, finalValue);
        if(continuousCommand!=null){
            continuousCommand.execute();
        }
    }

    @Override
    public void unexecute() {
        if(continuousCommand!=null){
            continuousCommand.unexecute();//this will take care of reverting back to initial value
        }
        temporalMetadata.getKeyframePane().removeKeyframe(keyframeCreated);
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
        this.finalValue = finalValue;
    }

    /**
     * Checks if this command has been filled or not
     * @return true if this 'Add Keyframe' command received the end of the change
     */
    public boolean isComplete(){
        return manual||continuousCommand!=null;
    }
}
