package com.nikhil.command;


import com.nikhil.view.custom.keyframe.KeyframeView;


/**
 * Command that adds a keyframe. This command can be of two types: manual or continuous.
 * <p>
 * Manual keyframes don't introduce any change, they just add a new keyframe.
 * The initial value and final value for this change is the same.
 * </p>
 * <p>
 * Continuous keyframe implies a change taking place along with the addition of this keyframe.
 * The initial and final value of the keyframe are initially the same. But at the end of the continuous change,
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
 * Created by NikhilVerma on 22/11/15.
 */
public abstract class AddKeyframe extends Command{
    protected boolean manual;
    protected Command continuousCommand;

    @Override
    public void execute() {

        KeyframeView keyframeCreated=getKeyframeCreated();
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
        KeyframeView keyframeCreated=getKeyframeCreated();
        keyframeCreated.removeFromParentKeyframePane();
    }

    public Command getContinuousCommand() {
        return continuousCommand;
    }

    /**
     * Executes and unexecutes a continuous command along with "this" command.
     * This is only allowed if this is not a manual keyframe
     * @param continuousCommand  Do not set a new continuous command if one already
     *                           exists and "this" command is on stack.
     */
    public void setContinuousCommand(Command continuousCommand) {
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

    public abstract KeyframeView getKeyframeCreated();

    /**
     * Checks if this command has been filled or not
     * @return true if this 'Add Keyframe' command received the end of the change
     */
    public boolean isComplete(){
        return manual||continuousCommand!=null;
    }
}
