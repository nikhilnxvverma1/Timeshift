package com.nikhil.command;

import com.nikhil.controller.ShapeViewController;
import com.nikhil.timeline.KeyValue;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public class ScaleShape extends TemporalActionOnSingleItem {

    private double initialScale;
    private double finalScale;


    public ScaleShape(ShapeViewController shapeViewController,double initialScale, double finalScale) {
        super(shapeViewController);
        this.initialScale = initialScale;
        this.finalScale = finalScale;
    }

    @Override
    public void execute() {
        double dScale = finalScale - initialScale;
        shapeViewController.scaleBy(dScale);
    }

    @Override
    public void unexecute() {
        double dScale = initialScale - finalScale;
        shapeViewController.scaleBy(dScale);
    }

    @Override
    public KeyValue getInitialValue() {
        return new KeyValue(initialScale);
    }

    @Override
    public KeyValue getFinalValue() {
        return new KeyValue(finalScale);
    }
}
