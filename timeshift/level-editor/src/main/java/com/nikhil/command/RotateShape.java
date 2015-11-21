package com.nikhil.command;

import com.nikhil.controller.ShapeViewController;
import com.nikhil.timeline.KeyValue;

/**
 * Created by NikhilVerma on 23/09/15.
 */
public class RotateShape extends TemporalActionOnSingleItem {

    private double initialAngle;
    private double finalAngle;

    public RotateShape(ShapeViewController shapeViewController, double initialAngle, double finalAngle) {
        super(shapeViewController);
        this.initialAngle = initialAngle;
        this.finalAngle = finalAngle;
    }

    @Override
    public void execute() {
        double dRotation=finalAngle-initialAngle;
        shapeViewController.rotateBy(dRotation);
        makeSelectionOfThisShape();
    }

    @Override
    public void unexecute() {
        double dRotation= initialAngle - finalAngle;
        shapeViewController.rotateBy(dRotation);
        makeSelectionOfThisShape();
    }

    @Override
    public KeyValue getInitialValue() {
        return new KeyValue(initialAngle);
    }

    @Override
    public KeyValue getFinalValue() {
        return new KeyValue(finalAngle);
    }
}
