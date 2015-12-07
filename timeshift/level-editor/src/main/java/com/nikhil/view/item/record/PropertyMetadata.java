package com.nikhil.view.item.record;

import com.nikhil.command.keyframe.DisableStopWatch;
import com.nikhil.command.keyframe.EnableStopWatch;
import com.nikhil.controller.CompositionViewController;
import com.nikhil.controller.ItemViewController;
import com.nikhil.timeline.change.KeyframeChangeNode;
import com.nikhil.timeline.keyframe.Keyframe;
import com.nikhil.view.custom.keyframe.KeyframeView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * Base class for each property.<b>Sub classes must call postInit() at the end of their constructor</b>
 * Created by NikhilVerma on 07/12/15.
 */
public abstract class PropertyMetadata extends Metadata{

    private CheckBox keyframable;

    //TODO we might not need these, on remove from the scene graph , the buttons will be garbage collected
    private EventHandler<ActionEvent> selectPreviousKeyframe=e->{
        itemViewController.getCompositionViewController().getKeyframeTable().resetSelectionOfEachExcept(getKeyframePane());
        final KeyframeView previousKeyframe=getKeyframePane().selectPreviousKeyframe();
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
            Keyframe nearbyKeyframe = getKeyframeChangeNode().findNearbyKeyframe(currentTime,
                    CompositionViewController.NEGLIGIBLE_TIME_DIFFERENCE);

            //create a command to add a manual keyframe at this time
            if(nearbyKeyframe==null){
                pushManualKeyframeCommand(currentTime);
            }
            //else ignore if a nearby keyframe already exists

        }
    };

    public PropertyMetadata(ItemViewController itemViewController, MetadataTag tag) {
        super(itemViewController,tag.toString(), tag);
    }

    /**Must be called by the sub class after the constructor has finished initializing*/
    protected void postInit() {
        this.keyframable=new CheckBox();
        this.keyframable.setSelected(!getKeyframeChangeNode().isEmpty());
        this.keyframable.setOnAction(event -> {
            if(!itemViewController.isInteractive()){
                //undo the selection that just got made
                keyframable.setSelected(!keyframable.isSelected());
                return;
            }
            if(keyframable.isSelected()){
                double currentTime=itemViewController.getCompositionViewController().getTime();
                KeyframeView newKeyframe=createNewKeyframe(currentTime);
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

    public abstract KeyframeChangeNode getKeyframeChangeNode();

    public abstract void pushManualKeyframeCommand(double time);

    /**
     * Creates and returns a new keyframe view for
     * the current value in the keyframe  pane
     * @param time the time of the keyframe
     * @return a keyframe view
     */
    protected abstract KeyframeView createNewKeyframe(double time) ;

}
