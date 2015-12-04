package com.nikhil.controller;

import com.nikhil.view.custom.DraggableTextValue;
import com.nikhil.view.custom.DraggableTextValueDelegate;
import com.nikhil.view.custom.ThumbSeeker;
import com.nikhil.view.custom.ThumbSeekerDelegate;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

/**
 * Created by NikhilVerma on 24/11/15.
 */
public class Playback implements ThumbSeekerDelegate,DraggableTextValueDelegate {

    public static final double FRAME_DELAY = 16;

    private CompositionViewController compositionViewController;
    private ThumbSeeker thumbSeeker;
    private DraggableTextValue timeDragger;
    private boolean currentlyPlaying=false;
    private double playerTime =0;

    public Playback(CompositionViewController compositionViewController,ThumbSeeker thumbSeeker) {
        this.compositionViewController = compositionViewController;
        this.thumbSeeker=thumbSeeker;
        this.timeDragger=createTimeDragger();
        runPlayer();
    }

    private DraggableTextValue createTimeDragger(){
        DraggableTextValue timeDragger=new DraggableTextValue(this);
        timeDragger.setValue(0);
        timeDragger.setLowerLimitExists(true);
        timeDragger.setLowerLimit(0);
        timeDragger.setUpperLimitExists(true);
        timeDragger.setUpperLimit(compositionViewController.getDuration());
        timeDragger.setStep(0.2);
        return timeDragger;
    }

    public DraggableTextValue getTimeDragger() {
        return timeDragger;
    }

    private void runPlayer() {
        Timeline timeline=new Timeline(new KeyFrame(Duration.millis(FRAME_DELAY), actionEvent -> {
            if(currentlyPlaying){
                playerTime +=FRAME_DELAY/1000;
                currentlyPlaying=seekTo(playerTime);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private boolean withinLimits(double time){
        return time>=0&&time<=compositionViewController.getDuration();
    }

    @Override
    public void thumbWasSeeked(ThumbSeeker thumbSeeker, double oldValue, double newValue) {
        double currentTime = compositionViewController.getTime();
        seekTo(currentTime);
        playerTime=currentTime;
    }


    /**
     * Handler for the play/pause button.
     * This method should only be called by composition view controller
     * @param actionEvent event associated with the handler
     */
    public void togglePlayback(ActionEvent actionEvent,ToggleButton playPause) {
        currentlyPlaying=playPause.isSelected();

        //reset if it reached time limit
        if(currentlyPlaying&& !withinLimits(playerTime)){
            playerTime=0;
        }
    }

    /**
     * Moves the player to the specified time(if its allowed)
     * @param time the time to which the composition should be moved.
     * @return true if the specified time is within limits(as in under duration of composition)
     * and the player did seek
     */
    public boolean seekTo(double time){
        if(withinLimits(playerTime)){
            compositionViewController.getCompositionController().getTimeline().setTime(time);
            compositionViewController.getWorkspace().getSelectedItems().updateView();
            thumbSeeker.setCurrentValueAcross(time, compositionViewController.getDuration());
            timeDragger.setValue(time);
            return true;
        }else{
            return false;
        }
    }

    public double getTime(){
        return thumbSeeker.getCurrentValueAcross(compositionViewController.getDuration());
    }

    @Override
    public void valueBeingDragged(DraggableTextValue draggableTextValue, double initialValue, double oldValue, double newValue) {
        if(draggableTextValue==timeDragger){
            seekTo(newValue);
        }
    }

    @Override
    public void valueFinishedChanging(DraggableTextValue draggableTextValue, double initialValue, double finalValue, boolean dragged) {
        if(draggableTextValue==timeDragger){
            seekTo(finalValue);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
