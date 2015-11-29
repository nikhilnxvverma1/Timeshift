package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.util.modal.UtilPoint;

import java.util.LinkedList;
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
        LinkedList<ItemViewController> linkedList=new LinkedList<>();
        linkedList.add(itemViewController);
        return linkedList;
    }

    public abstract UtilPoint getInitialPoint();
    public abstract UtilPoint getFinalPoint();
}
