package com.nikhil.controller;

import com.nikhil.logging.Logger;
import com.nikhil.view.custom.ThumbSeeker;
import com.nikhil.view.custom.ThumbSeekerDelegate;

/**
 * Created by NikhilVerma on 24/11/15.
 */
public class Playback implements ThumbSeekerDelegate {

    private CompositionViewController compositionViewController;

    public Playback(CompositionViewController compositionViewController) {
        this.compositionViewController = compositionViewController;
    }

    @Override
    public void thumbWasSeeked(ThumbSeeker thumbSeeker, double oldValue, double newValue) {
        double step = newValue - oldValue;
//        Logger.log(step +" seeked");
        double currentTime = compositionViewController.getTime();
        compositionViewController.getCompositionController().getTimeline().setTime(currentTime);
        compositionViewController.getWorkspace().getSelectedItems().updateView();
    }
}
