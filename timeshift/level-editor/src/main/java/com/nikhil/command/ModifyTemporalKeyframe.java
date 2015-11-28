package com.nikhil.command;


import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.keyframe.TemporalKeyframeView;

/**
 *
 * Created by NikhilVerma on 13/11/15.
 */
public class ModifyTemporalKeyframe extends Command{
    private TemporalKeyframeView temporalKeyframeView;
    private KeyValue initialValue;
    private KeyValue finalValue;
    private TemporalActionOnSingleItem continuousCommand;

    public ModifyTemporalKeyframe(TemporalKeyframeView temporalKeyframeView, KeyValue initialValue) {
        this(temporalKeyframeView,initialValue,null,null);
    }

    public ModifyTemporalKeyframe(TemporalKeyframeView temporalKeyframeView, KeyValue initialValue, KeyValue finalValue, TemporalActionOnSingleItem continuousCommand) {
        this.temporalKeyframeView = temporalKeyframeView;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.continuousCommand = continuousCommand;
    }

    @Override
    public void execute() {
        temporalKeyframeView.getKeyframeModel().setKeyValue(finalValue);
        if(continuousCommand!=null){
            continuousCommand.execute();
        }
    }

    @Override
    public void unexecute() {
        if(continuousCommand!=null){
            continuousCommand.unexecute();//this will take care of reverting back to initial value
        }
        temporalKeyframeView.getKeyframeModel().setKeyValue(initialValue);
    }

    public TemporalKeyframeView getTemporalKeyframeView() {
        return temporalKeyframeView;
    }

    public void setTemporalKeyframeView(TemporalKeyframeView temporalKeyframeView) {
        this.temporalKeyframeView = temporalKeyframeView;
    }

    public TemporalActionOnSingleItem getContinuousCommand() {
        return continuousCommand;
    }

    public void setContinuousCommand(TemporalActionOnSingleItem continuousCommand) {
        this.continuousCommand = continuousCommand;
    }

    public KeyValue getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(KeyValue initialValue) {
        this.initialValue = initialValue;
    }

    public KeyValue getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(KeyValue finalValue) {
        this.finalValue = finalValue;
    }

    /**
     * Checks if this command has been filled or not
     * @return true if this 'Modify Keyframe' command received the end of the change
     */
    public boolean isComplete(){
        return continuousCommand!=null;
    }
}
