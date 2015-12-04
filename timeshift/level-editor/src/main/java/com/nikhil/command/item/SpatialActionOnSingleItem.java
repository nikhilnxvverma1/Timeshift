package com.nikhil.command.item;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;

import java.util.List;

/**
 * Created by NikhilVerma on 28/11/15.
 */
public abstract class SpatialActionOnSingleItem extends ItemCommand {

    protected ItemViewController itemViewController;

    public SpatialActionOnSingleItem(ItemViewController itemViewController) {
        this.itemViewController = itemViewController;
    }

    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(itemViewController);
    }

    public abstract UtilPoint getInitialPoint();
    public abstract UtilPoint getFinalPoint();
}
