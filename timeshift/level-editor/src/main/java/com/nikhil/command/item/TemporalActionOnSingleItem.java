package com.nikhil.command.item;

import com.nikhil.command.item.ItemCommand;
import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.timeline.KeyValue;

import java.util.List;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public abstract class TemporalActionOnSingleItem extends ItemCommand {

    @Override
    public List<ItemViewController> getItemList() {
        return listForSingleItem(getItemViewController());
    }

    public abstract KeyValue getInitialValue();
    public abstract KeyValue getFinalValue();

    public abstract ItemViewController getItemViewController();
}
