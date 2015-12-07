package com.nikhil.command.keyframe;


import com.nikhil.command.item.ItemCommand;
import com.nikhil.command.item.TemporalActionOnSingleItem;
import com.nikhil.controller.ItemViewController;
import com.nikhil.timeline.KeyValue;
import com.nikhil.view.custom.keyframe.TemporalKeyframeView;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by NikhilVerma on 13/11/15.
 */
public class ModifyTemporalKeyframe extends ItemCommand {
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
        temporalKeyframeView.getKeyframeModel().setKeyValue(initialValue);
        if(continuousCommand!=null){
            continuousCommand.unexecute();//this will take care of reverting back to initial value
        }
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

    @Override
    public List<ItemViewController> getItemList() {
        LinkedList<ItemViewController> list=new LinkedList<>();
        list.add(temporalKeyframeView.getKeyframePane().getMetadata().getItemViewController());
        return list;
    }
}
