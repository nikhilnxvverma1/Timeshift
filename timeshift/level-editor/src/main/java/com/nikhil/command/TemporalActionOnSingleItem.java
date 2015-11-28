package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.timeline.KeyValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public abstract class TemporalActionOnSingleItem extends ItemCommand{
    protected ShapeViewController shapeViewController;

    public TemporalActionOnSingleItem(ShapeViewController shapeViewController) {
        this.shapeViewController = shapeViewController;
    }

    @Override
    public List<ItemViewController> getItemList() {
        LinkedList<ItemViewController> list=new LinkedList<>();
        list.add(shapeViewController);
        return list;
    }

    public abstract KeyValue getInitialValue();
    public abstract KeyValue getFinalValue();

}
