package com.nikhil.command.item;

import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;

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
        movePossiblyKeyframableItem(itemViewController,finalPosition.getX(),finalPosition.getY());
//        itemViewController.moveTo(finalPosition.getX(), finalPosition.getY());
    }

    @Override
    public void unexecute() {
        movePossiblyKeyframableItem(itemViewController,initialPosition.getX(),initialPosition.getY());
//        itemViewController.moveTo(initialPosition.getX(),initialPosition.getY());
    }

    @Override
    public UtilPoint getInitialPoint() {
        return initialPosition;
    }

    @Override
    public UtilPoint getFinalPoint() {
        return finalPosition;
    }

    /**
     * Moves the item ,taking into account that it may be keyframable and in which case, exact movement is
     * defined by the translation change node with respect to the current time of the composition.
     * @param itemViewController item to move
     * @param x new x of item(only used if the item is not keyframable)
     * @param y new y of item(only used if the item is not keyframable)
     */
    private void movePossiblyKeyframableItem(ItemViewController itemViewController, double x, double y) {
        SpatialMetadata translationMetadata = itemViewController.getSpatialMetadata(MetadataTag.TRANSLATION);
        if(translationMetadata.isKeyframable()){
            final double currentTime = itemViewController.getCompositionViewController().getTime();
            translationMetadata.getKeyframeChangeNode().setTime(currentTime);
        }else{
            UtilPoint translation = itemViewController.getTranslation();
            itemViewController.moveTo(x, y);
        }
    }
}
