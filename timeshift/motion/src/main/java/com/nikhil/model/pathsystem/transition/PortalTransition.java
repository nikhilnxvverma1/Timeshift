package com.nikhil.model.pathsystem.transition;

/**
 * Created by NikhilVerma on 14/08/15.
 */
public class PortalTransition extends Transition {
    protected double fromDistance;
    protected double toDistance;

    public double getFromDistance() {
        return fromDistance;
    }

    public void setFromDistance(double fromDistance) {
        this.fromDistance = fromDistance;
    }

    public double getToDistance() {
        return toDistance;
    }

    public void setToDistance(double toDistance) {
        this.toDistance = toDistance;
    }

    @Override
    public TransitionType getTransitionType() {
        return TransitionType.PORTAL;
    }
}
