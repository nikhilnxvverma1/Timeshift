package com.nikhil.controller;

import com.nikhil.logging.Logger;
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
public class Playback implements ThumbSeekerDelegate {

    public static final double FRAME_DELAY = 16;

    private CompositionViewController compositionViewController;
    private ThumbSeeker thumbSeeker;
    private boolean currentlyPlaying=false;
    private double playerTime =0;

    public Playback(CompositionViewController compositionViewController,ThumbSeeker thumbSeeker) {
        this.compositionViewController = compositionViewController;
        this.thumbSeeker=thumbSeeker;
        runPlayer();
    }

    private void runPlayer() {
        Timeline timeline=new Timeline(new KeyFrame(Duration.millis(FRAME_DELAY), actionEvent -> {
            if(currentlyPlaying){
                playerTime +=FRAME_DELAY/1000;
                if(underMaxDuration(playerTime)){
                    compositionViewController.getCompositionController().getTimeline().setTime(playerTime);
                    compositionViewController.getWorkspace().getSelectedItems().updateView();
                    thumbSeeker.setCurrentValueAcross(playerTime, compositionViewController.getDuration());
                }else{
                    currentlyPlaying=false;
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private boolean underMaxDuration(double time){
        return time<=compositionViewController.getDuration();
    }

    public ThumbSeeker getThumbSeeker() {
        return thumbSeeker;
    }

    @Override
    public void thumbWasSeeked(ThumbSeeker thumbSeeker, double oldValue, double newValue) {
        double currentTime = compositionViewController.getTime();
        compositionViewController.getCompositionController().getTimeline().setTime(currentTime);
        compositionViewController.getWorkspace().getSelectedItems().updateView();
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
        if(currentlyPlaying&& !underMaxDuration(playerTime)){
            playerTime=0;
        }
    }
}
