package com.nikhil.command.item;

import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;

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
        itemViewController.moveTo(finalPosition.getX(), finalPosition.getY());
    }

    @Override
    public void unexecute() {
        itemViewController.moveTo(initialPosition.getX(),initialPosition.getY());
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
