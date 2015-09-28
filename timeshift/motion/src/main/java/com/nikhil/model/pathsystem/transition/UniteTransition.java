package com.nikhil.model.pathsystem.transition;

/**
 * Created by NikhilVerma on 14/08/15.
 */
public class UniteTransition extends Transition {

    protected double distanceInTargetPath;

    public double getDistanceInTargetPath() {
        return distanceInTargetPath;
    }

    public void setDistanceInTargetPath(double distanceInTargetPath) {
        this.distanceInTargetPath = distanceInTargetPath;
    }

    @Override
    public TransitionType getTransitionType() {
        return TransitionType.UNITE;
    }
}
