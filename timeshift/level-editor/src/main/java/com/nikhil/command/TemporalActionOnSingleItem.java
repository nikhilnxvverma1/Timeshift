package com.nikhil.command;

import com.nikhil.controller.ItemViewController;
import com.nikhil.controller.ShapeViewController;
import com.nikhil.editor.selection.SelectedItems;
import com.nikhil.timeline.KeyValue;

/**
 * Created by NikhilVerma on 21/09/15.
 */
public abstract class TemporalActionOnSingleItem implements Command{//TODO dedicate this class only for temporal actions?
    protected ShapeViewController shapeViewController;

    public TemporalActionOnSingleItem(ShapeViewController shapeViewController) {
        this.shapeViewController = shapeViewController;
    }

    protected void makeSelectionOfThisShape(){
        SelectedItems selectedItems = shapeViewController.getCompositionViewController().getWorkspace().getSelectedItems();
        selectedItems.clearSelection();
        selectedItems.addToSelection(shapeViewController);
    }
    public abstract KeyValue getInitialValue();
    public abstract KeyValue getFinalValue();

}
