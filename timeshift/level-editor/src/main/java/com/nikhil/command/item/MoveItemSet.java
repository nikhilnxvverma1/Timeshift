package com.nikhil.command.item;

import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;
import com.nikhil.view.item.record.MetadataTag;
import com.nikhil.view.item.record.SpatialMetadata;
import javafx.geometry.Point2D;

import java.util.Set;

/**
 * Created by NikhilVerma on 20/09/15.
 */
public class MoveItemSet extends ActionOnItemSet {

    private Point2D from;
    private Point2D to;

    public MoveItemSet(Set<ItemViewController> itemSet, Point2D from, Point2D to) {
        super(itemSet);
        this.from=from;
        this.to=to;
    }

    @Override
    public void execute() {

        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        for (ItemViewController itemViewController : itemSet) {
            movePossiblyKeyframableItem(itemViewController, dx, dy);
        }
    }

    @Override
    public void unexecute() {

        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        for (ItemViewController itemViewController : itemSet) {
            movePossiblyKeyframableItem(itemViewController, dx, dy);
        }
    }

    /**
     * Moves the item ,taking into account that it may be keyframable and in which case, exact movement is
     * defined by the translation change node with respect to the current time of the composition.
     * @param itemViewController item to move
     * @param dx distance to shift horizontally(only used if the item is not keyframable)
     * @param dy distance to shift vertically(only used if the item is not keyframable)
     */
    private void movePossiblyKeyframableItem(ItemViewController itemViewController, double dx, double dy) {
        SpatialMetadata translationMetadata = itemViewController.getSpatialMetadata(MetadataTag.TRANSLATION);
        if(translationMetadata.isKeyframable()){
            final double currentTime = itemViewController.getCompositionViewController().getTime();
            translationMetadata.getSpatialKeyframeChangeNode().setTime(currentTime);
        }else{
            UtilPoint translation = itemViewController.getTranslation();
            itemViewController.moveTo(translation.getX()+dx, translation.getY()+dy);
        }
    }


}
