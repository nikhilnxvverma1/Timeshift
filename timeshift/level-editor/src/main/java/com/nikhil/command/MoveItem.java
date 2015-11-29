package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple command to move a single item from an initial position to a final position
 * Created by NikhilVerma on 28/11/15.
 */
public class MoveItem extends SpatialActionOnSingleItem{

    private UtilPoint initialPosition;
    private UtilPoint finalPosition;

    public MoveItem(ItemViewController itemViewController, UtilPoint initialPosition, UtilPoint finalPosition) {
        super(itemViewController);
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }

    @Override
    public void execute() {
        double dx = finalPosition.getX() - initialPosition.getX();
        double dy = finalPosition.getY() - initialPosition.getY();
        itemViewController.moveBy(dx, dy);
    }

    @Override
    public void unexecute() {
        double dx = initialPosition.getX() - finalPosition.getX();
        double dy = initialPosition.getY() - finalPosition.getY();
        itemViewController.moveBy(dx, dy);
    }

    @Override
    public UtilPoint getInitialPoint() {
        return initialPosition;
    }

    @Override
    public UtilPoint getFinalPoint() {
        return finalPosition;
    }
}
