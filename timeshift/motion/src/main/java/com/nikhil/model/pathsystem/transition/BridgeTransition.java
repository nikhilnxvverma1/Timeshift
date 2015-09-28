package com.nikhil.model.pathsystem.transition;

import com.nikhil.model.pathsystem.TravelPath;

/**
 * Created by NikhilVerma on 14/08/15.
 */
public class BridgeTransition extends Transition {

    protected double openingTimeForStart;
    protected double closingTimeForStart;
    protected double openingTimeForEnd;
    protected double cosingTimeForEnd;

    public double getOpeningTimeForStart() {
        return openingTimeForStart;
    }

    public void setOpeningTimeForStart(double openingTimeForStart) {
        this.openingTimeForStart = openingTimeForStart;
    }

    public double getOpeningTimeForEnd() {
        return openingTimeForEnd;
    }

    public void setOpeningTimeForEnd(double openingTimeForEnd) {
        this.openingTimeForEnd = openingTimeForEnd;
    }

    public double getClosingTimeForStart() {
        return closingTimeForStart;
    }

    public void setClosingTimeForStart(double closingTimeForStart) {
        this.closingTimeForStart = closingTimeForStart;
    }

    public double getCosingTimeForEnd() {
        return cosingTimeForEnd;
    }

    public void setCosingTimeForEnd(double cosingTimeForEnd) {
        this.cosingTimeForEnd = cosingTimeForEnd;
    }

    @Override
    public TransitionType getTransitionType() {
        return TransitionType.BRIDGE;
    }
}
