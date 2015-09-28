package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.selection.SelectedItems;

import java.util.Set;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public class ScaleShape extends ActionOnSingleItem{

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
        makeSelectionOfThisShape();
    }

    @Override
    public void unexecute() {
        double dScale = initialScale - finalScale;
        shapeViewController.scaleBy(dScale);
        makeSelectionOfThisShape();
    }

}
