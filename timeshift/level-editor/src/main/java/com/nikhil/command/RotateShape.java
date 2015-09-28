package com.nikhil.command;

import com.nikhil.controller.ShapeViewController;

/**
 * Created by NikhilVerma on 23/09/15.
 */
public class RotateShape extends ActionOnSingleItem {

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
}
